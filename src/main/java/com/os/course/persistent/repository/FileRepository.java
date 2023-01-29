package com.os.course.persistent.repository;

import com.os.course.model.entity.Mp3File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<Mp3File, Long> {
}
