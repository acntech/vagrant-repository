package no.acntech.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import no.acntech.model.Box;
import no.acntech.model.SearchOrder;
import no.acntech.model.SearchProviderType;
import no.acntech.model.SearchBoxResult;
import no.acntech.model.SearchSort;
import no.acntech.service.DecoratorService;
import no.acntech.service.SearchService;

@RequestMapping(path = "/api/v1/search")
@RestController
public class SearchResource {

    private final SearchService searchService;
    private final DecoratorService decoratorService;

    public SearchResource(final SearchService searchService,
                          final DecoratorService decoratorService) {
        this.searchService = searchService;
        this.decoratorService = decoratorService;
    }

    @GetMapping
    public ResponseEntity<SearchBoxResult> searchBoxes(@RequestParam(name = "q", required = false) final String q,
                                                       @RequestParam(name = "provider", required = false) final String provider,
                                                       @RequestParam(name = "sort", defaultValue = "downloads") final String sort,
                                                       @RequestParam(name = "order", defaultValue = "desc") final String order,
                                                       @RequestParam(name = "limit", defaultValue = "10") final Integer limit,
                                                       @RequestParam(name = "page", defaultValue = "1") final Integer page,
                                                       final UriComponentsBuilder uriBuilder) {
        final var searchProvider = SearchProviderType.fromProvider(provider);
        final var searchSort = SearchSort.fromSort(sort);
        final var searchOrder = SearchOrder.fromOrder(order);
        final var boxes = searchService.searchBoxes(q, searchProvider, searchSort, searchOrder, limit, page);
        final var decoratedBoxes = boxes.stream()
                .map(box -> decorateBox(box, uriBuilder))
                .toList();
        return ResponseEntity.ok(new SearchBoxResult(decoratedBoxes));
    }

    private Box decorateBox(final Box box,
                            final UriComponentsBuilder uriBuilder) {
        final var currentVersion = searchService.searchCurrentVersion(box.id());
        if (currentVersion != null) {
            final var decoratedCurrentVersion = decoratorService.decorateVersion(box, currentVersion, uriBuilder);
            return box.with(decoratedCurrentVersion, null);
        } else {
            return box;
        }
    }
}
