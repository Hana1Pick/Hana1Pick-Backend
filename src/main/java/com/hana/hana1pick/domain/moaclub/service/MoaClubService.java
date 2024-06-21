package com.hana.hana1pick.domain.moaclub.service;

import com.hana.hana1pick.domain.moaclub.repository.MoaClubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MoaClubService {

    private final MoaClubRepository moaClubRepository;
}
