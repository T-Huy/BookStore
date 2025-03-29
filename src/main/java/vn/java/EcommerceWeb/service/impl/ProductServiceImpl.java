package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vn.java.EcommerceWeb.dto.request.ProductRequest;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.ProductResponse;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.model.*;
import vn.java.EcommerceWeb.repository.AuthorRepository;
import vn.java.EcommerceWeb.repository.CategoryRepository;
import vn.java.EcommerceWeb.repository.ProductRepository;
import vn.java.EcommerceWeb.repository.PublisherRepository;
import vn.java.EcommerceWeb.service.CloudinaryService;
import vn.java.EcommerceWeb.service.ProductService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final AuthorRepository authorRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Long createProduct(ProductRequest request, MultipartFile file) throws IOException {
        log.info("Product start creating");
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));
        if (productRepository.existsByNameAndPublisherId(request.getName(), request.getPublisherId())) {
            log.error("Product already exists {}", request.getName());
            throw new ResourceNotFoundException("Product already exists in Publisher");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String imageUrl = cloudinaryService.uploadFile(file);
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .description(request.getDescription())
                .image(imageUrl)
                .category(category)
                .publisher(publisher)
                .build();

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            List<Author> authors = authorRepository.findAllById(request.getAuthorIds());
            if (authors.size() != request.getAuthorIds().size()) {
                throw new ResourceNotFoundException("Some author not found");
            }
            Set<ProductAuthor> productAuthors = authors.stream()
                    .map(author -> ProductAuthor.builder().product(product).author(author).build())
                    .collect(Collectors.toSet());
            product.setProductAuthors(productAuthors);
        }
        productRepository.save(product);
        log.info("Product created successfully");
        return product.getId();
    }

    @Override
    public void updateProduct(Long id, ProductRequest request, MultipartFile file) throws IOException {
        log.info("Product start updating");
        Product product = getProductById(id);
        boolean isChangingPublisher = !product.getPublisher().getId().equals(request.getPublisherId());
        boolean isNameChanged = !product.getName().equals(request.getName());
        // Kiểm tra trùng tên trong cùng publisher
        if (!isChangingPublisher && isNameChanged && productRepository.existsByNameAndPublisherId(request.getName(), request.getPublisherId())) {
            log.error("Product already exists {}", request.getName());
            throw new ResourceNotFoundException("Product already exists in Publisher");
        }
        // Kiểm tra trùng tên trong publisher mới nếu có đổi publisher
        if (isChangingPublisher && productRepository.existsByNameAndPublisherId(request.getName(), request.getPublisherId())) {
            log.error("Product name already exists in the new publisher {}", request.getName());
            throw new ResourceNotFoundException("Product name already exists in the new publisher");
        }
        Publisher publisher = publisherRepository.findById(request.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String imageUrl = product.getImage();
        if (!StringUtils.isEmpty(file)) {
            cloudinaryService.deleteFile(imageUrl);
            imageUrl = cloudinaryService.uploadFile(file);
            product.setImage(imageUrl);
        }
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setPublisher(publisher);

        if (request.getAuthorIds() != null) {
            Set<Long> newAuthorIds = new HashSet<>(request.getAuthorIds());
            Set<Long> currentAuthorIds = product.getProductAuthors()
                    .stream()
                    .map(productAuthor -> productAuthor.getAuthor().getId())
                    .collect(Collectors.toSet());

            if (!newAuthorIds.equals(currentAuthorIds)) {
                List<Author> authors = authorRepository.findAllById(newAuthorIds);
                if (authors.size() != newAuthorIds.size()) {
                    throw new ResourceNotFoundException("Some author not found");
                }
                product.getProductAuthors().clear();
                Set<ProductAuthor> newProductAuthors = authors.stream()
                        .map(author -> ProductAuthor.builder().product(product).author(author).build())
                        .collect(Collectors.toSet());
                product.setProductAuthors(newProductAuthors);
            }
        }
        productRepository.save(product);
        log.info("Product updated successfully");
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Product start deleting");
        getProductById(id);
        productRepository.deleteById(id);
        log.info("Product deleted successfully");

    }

    @Override
    public ProductResponse getDetailProduct(Long id) {
        log.info("Product start getting detail");
        Product product = getProductById(id);
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .description(product.getDescription())
                .image(product.getImage())
                .category(product.getCategory().getName())
                .publisher(product.getPublisher().getName())
                .authors(product.getProductAuthors()
                        .stream()
                        .map(productAuthor -> productAuthor.getAuthor().getName())
                        .collect(Collectors.toList()))
                .build();
        log.info("Product get detail successfully");
        return productResponse;
    }

    @Override
    public PageResponse<?> getAllProduct(String keyword, Long categoryId, Double minPrice, Double maxPrice, int pageNo,
                                         int pageSize, String sortBy) {
        log.info("Product start getting all");
        if (pageNo > 0) {
            pageNo = pageNo - 1;
        }
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase(("asc"))) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
        Page<Product> products = productRepository.searchAndFilterProduct(keyword, categoryId, minPrice, maxPrice, pageable);
        List<ProductResponse> responses = products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .category(product.getCategory().getName())
                        .publisher(product.getPublisher().getName())
                        .authors(product.getProductAuthors()
                                .stream()
                                .map(productAuthor -> productAuthor.getAuthor().getName())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        log.info("Get all products successfully");
        return PageResponse.builder()
                .pageNo(pageNo + 1)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .totalElement(products.getTotalElements())
                .items(responses)
                .build();
    }

    @Override
    public PageResponse<?> getAllProduct(int pageNo, int pageSize, String sortBy) {
        log.info("Product start getting all");
        if (pageNo > 0) {
            pageNo = pageNo - 1;
        }
        List<Sort.Order> sorts = new ArrayList<>();
        if (StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                if (matcher.group(3).equalsIgnoreCase(("asc"))) {
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                } else {
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sorts));
        Page<Product> products = productRepository.findAll(pageable);
        List<ProductResponse> responses = products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .description(product.getDescription())
                        .image(product.getImage())
                        .category(product.getCategory().getName())
                        .publisher(product.getPublisher().getName())
                        .authors(product.getProductAuthors()
                                .stream()
                                .map(productAuthor -> productAuthor.getAuthor().getName())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
        log.info("Get all products successfully");
        return PageResponse.builder()
                .pageNo(pageNo + 1)
                .pageSize(pageSize)
                .totalPage(products.getTotalPages())
                .totalElement(products.getTotalElements())
                .items(responses)
                .build();
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
