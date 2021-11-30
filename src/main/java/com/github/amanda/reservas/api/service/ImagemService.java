package com.github.amanda.reservas.api.service;

import com.github.amanda.reservas.api.domain.Imagem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "some-random-api", url = "https://some-random-api.ml/img/dog")
public interface ImagemService {

    @RequestMapping
    @GetMapping()
    Imagem obterImagem();
}
