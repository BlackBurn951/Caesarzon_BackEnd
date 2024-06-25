package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Dao.SearchRepository;
import org.caesar.productservice.Data.Entities.Search;
import org.caesar.productservice.Data.Services.SearchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    @Override
    public List<String> getAllSearchs(String username) {
        try{
            List<String>  searchList = new Vector<>();
            List<Search> searches = searchRepository.findAllByUsername(username);
            for(Search s: searches){
                searchList.add(s.getSearchText());
            }
            return  searchList;
        }catch(Exception | Error e){
            System.out.println("Errore nella presa delle ricerche");
            return null;
        }

    }
}
