package com.mbc.repository;

import com.mbc.entity.MemberImg;
import com.mbc.entity.Review;
import com.mbc.entity.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {
    ReviewImg findByReviewId(Long reviewId);

    List<ReviewImg> findByReview(Review review);
}
