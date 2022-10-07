package no.acntech.model;

public enum SearchOrder {

    ASC("asc"),
    DESC("desc");

    private final String order;

    SearchOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return order;
    }

    public static SearchOrder fromOrder(final String order) {
        for (SearchOrder searchOrder : SearchOrder.values()) {
            if (searchOrder.order.equals(order)) {
                return searchOrder;
            }
        }
        return DESC;
    }
}
