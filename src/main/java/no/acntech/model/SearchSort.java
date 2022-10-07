package no.acntech.model;

public enum SearchSort {

    DOWNLOADS("downloads"),
    CREATED("created"),
    UPDATED("updated");

    private final String sort;

    SearchSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return sort;
    }

    public static SearchSort fromSort(final String sort) {
        for (SearchSort searchSort : SearchSort.values()) {
            if (searchSort.sort.equals(sort)) {
                return searchSort;
            }
        }
        return DOWNLOADS;
    }
}
