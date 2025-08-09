package com.example.capstone.utils;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtils {
    private PaginationUtils() {
    }

    // Build a compact pager with ellipses. Returns a List<Object> of Integer and "…" (String).
    public static List<Object> computePageNumbers(Page<?> page) {
        int total = page.getTotalPages();
        int current = page.getNumber();
        int maxButtons = 7;

        List<Object> out = new ArrayList<>();
        if (total <= 1) return out; // nothing to render

        if (total <= maxButtons) {
            for (int i = 0; i < total; i++) out.add(i);
            return out;
        }

        // Always show first & last, +/-1 around current, with gaps
        out.add(0);

        int left = Math.max(1, current - 1);
        int right = Math.min(total - 2, current + 1);

        if (left > 1) out.add("…");
        for (int i = left; i <= right; i++) out.add(i);
        if (right < total - 2) out.add("…");

        out.add(total - 1);
        return out;
    }

}
