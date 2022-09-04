package com.skokcmd.service;

import java.util.List;

public interface IWebScrapingService<T, S> {
    List<T> getItemsFromRequest(S request);
}
