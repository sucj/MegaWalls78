package icu.suc.megawalls78.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SupplierComponent implements ComponentLike {

    private final Supplier<Component> supplier;

    SupplierComponent(Supplier<Component> supplier) {
        this.supplier = supplier;
    }

    public static ComponentLike create(Supplier<Component> supplier) {
        return new SupplierComponent(supplier);
    }

    @Override
    public @NotNull Component asComponent() {
        return supplier.get();
    }
}
