package vn.java.EcommerceWeb.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.java.EcommerceWeb.dto.request.PublisherRequest;
import vn.java.EcommerceWeb.dto.response.CategoryResponse;
import vn.java.EcommerceWeb.dto.response.PageResponse;
import vn.java.EcommerceWeb.dto.response.PublisherResponse;
import vn.java.EcommerceWeb.exception.ResourceNotFoundException;
import vn.java.EcommerceWeb.mapper.PublisherMapper;
import vn.java.EcommerceWeb.model.Category;
import vn.java.EcommerceWeb.model.Publisher;
import vn.java.EcommerceWeb.repository.PublisherRepository;
import vn.java.EcommerceWeb.service.PublisherService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;
    private final PublisherMapper publisherMapper;

    @Override
    public Long createPublisher(PublisherRequest request) {
        log.info("Category start creating");
        if (publisherRepository.existsByName(request.getName())) {
            log.error("Publisher already exists {}", request.getName());
            throw new ResourceNotFoundException("Publisher already exists");
        }
        Publisher publisher = publisherMapper.toPublisher(request);
        publisherRepository.save(publisher);
        log.info("Publisher created successfully");
        return publisher.getId();
    }

    @Override
    public void updatePublisher(Long id, PublisherRequest request) {
        log.info("Publisher start updating");
        getPublisherById(id);
        Publisher publisher = publisherMapper.toPublisher(request);
        publisherRepository.save(publisher);
        log.info("Publisher updated successfully");
    }

    @Override
    public void deletePublisher(Long id) {
        log.info("Publisher start deleting");
        Publisher publisher = getPublisherById(id);
        publisherRepository.delete(publisher);
        log.info("Publisher deleted successfully");
    }

    @Override
    public PublisherResponse getDetailPublisher(Long id) {
        log.info("Publisher start getting detail");
        Publisher publisher = getPublisherById(id);
        PublisherResponse publisherResponse = publisherMapper.toPublisherResponse(publisher);
        log.info("Publisher get detail successfully");
        return publisherResponse;
    }

    @Override
    public PageResponse<?> getAllPublisher(int pageNo, int pageSize, String sortBy) {
        if(pageNo > 0) {
            pageNo = pageNo -1;
        }
        List<Sort.Order> sorts = new ArrayList<>();
        if(StringUtils.hasLength(sortBy)) {
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
        Page<Publisher> publishers = publisherRepository.findAll(pageable);
        List<PublisherResponse> responses = publisherMapper.toPublisherResponseList(publishers.getContent());
        log.info("Get all publishers successfully");
        return PageResponse.builder()
                .pageNo(pageNo+1)
                .pageSize(pageSize)
                .totalPage(publishers.getTotalPages())
                .totalElement(publishers.getTotalElements())
                .items(responses)
                .build();
    }

    private Publisher getPublisherById(Long id) {
        return publisherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Publisher not found"));
    }
}
