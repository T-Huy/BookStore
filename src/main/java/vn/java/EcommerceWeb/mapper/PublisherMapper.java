package vn.java.EcommerceWeb.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import vn.java.EcommerceWeb.dto.request.PublisherRequest;
import vn.java.EcommerceWeb.dto.response.PublisherResponse;
import vn.java.EcommerceWeb.model.Publisher;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PublisherMapper {
    Publisher toPublisher(PublisherRequest request);
    PublisherResponse toPublisherResponse(Publisher publisher);
    List<PublisherResponse> toPublisherResponseList(List<Publisher> publishers);
}
