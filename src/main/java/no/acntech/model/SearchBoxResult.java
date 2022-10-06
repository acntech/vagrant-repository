package no.acntech.model;

import javax.validation.Valid;
import java.util.List;

public record SearchBoxResult(@Valid List<Box> boxes) {
}
