package net.hyze.core.shared.misc.youtube;

import com.google.common.primitives.Ints;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VideoInformation {

    private final Snippet snippet;
    private final Statistics statistics;

    @Getter
    @RequiredArgsConstructor
    public class Snippet {

        private final String publishedAt;
        private final String channelId;
        private final String title;
        private final String channelTitle;

    }

    @RequiredArgsConstructor
    public class Statistics {
        
        private final String viewCount;
        private final String likeCount;
        private final String dislikeCount;
        private final String commentCount;
        
        public int getViewCount(){
            return Ints.tryParse(this.viewCount);
        }
        
        public int getLikeCount(){
            return Ints.tryParse(this.likeCount);
        }
        
        public int getDislikeCount(){
            return Ints.tryParse(this.dislikeCount);
        }
        
        public int getCommentCount(){
            return Ints.tryParse(this.commentCount);
        }
        
    }

}
