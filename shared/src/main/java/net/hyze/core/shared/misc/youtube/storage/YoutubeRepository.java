package net.hyze.core.shared.misc.youtube.storage;

import net.hyze.core.shared.misc.youtube.VideoInformation;
import net.hyze.core.shared.misc.youtube.storage.spec.videos.InsertVideoSpec;
import net.hyze.core.shared.misc.youtube.storage.spec.videos.SelectVideoSpec;
import net.hyze.core.shared.misc.youtube.storage.spec.youtuber.DeleteYoutuberSpec;
import net.hyze.core.shared.misc.youtube.storage.spec.youtuber.InsertYoutuberSpec;
import net.hyze.core.shared.misc.youtube.storage.spec.youtuber.SelectYoutuberSpec;
import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;

public class YoutubeRepository extends MysqlRepository {

    public YoutubeRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public String isYoutuber(int userId) {
        return query(new SelectYoutuberSpec(userId));
    }

    public boolean deleteYoutuber(int userId) {
        return query(new DeleteYoutuberSpec(userId));
    }

    public void insertYoutuber(int userId, String channelId) {
        query(new InsertYoutuberSpec(userId, channelId));
    }

    public void insertYoutubeVideo(int userId, String channelId, String videoId, VideoInformation information) {
        query(new InsertVideoSpec(userId, channelId, videoId, information));
    }

    public boolean containsYoutubeVideo(String videoId) {
        return query(new SelectVideoSpec(videoId));
    }

}
