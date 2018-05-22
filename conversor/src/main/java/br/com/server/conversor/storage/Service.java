package br.com.server.conversor.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface Service {
	
    void initial();

    void store(MultipartFile file, String path);

    Resource load(String filename, String local_path);

    String path(String filename, String local_path);

}
