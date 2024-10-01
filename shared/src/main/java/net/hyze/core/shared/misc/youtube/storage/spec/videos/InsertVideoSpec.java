package net.hyze.core.shared.misc.youtube.storage.spec.videos;

import net.hyze.core.shared.CoreConstants;
import net.hyze.core.shared.misc.youtube.VideoInformation;
import net.hyze.core.shared.storage.repositories.specs.InsertSqlSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.KeyHolder;

@RequiredArgsConstructor
public class InsertVideoSpec extends InsertSqlSpec<Boolean> {

    private final int userId;
    private final String channelId;
    private final String videoId;
    private final VideoInformation information;

    @Override
    public Boolean parser(int affectedRows, KeyHolder keyHolder) {
        return affectedRows != 1;
    }

    @Override
    public PreparedStatementCreator getPreparedStatementCreator() {
        String query = "INSERT INTO `%s`(`user_id`, `channel_id`, `video_id`, `published_at`, `view_count`, `like_count`, `dislike_count`, `comment_count`, `created_at`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(
                    String.format(
                            query,
                            CoreConstants.Databases.Mysql.Tables.YOUTUBERS_VIDEOS_TABLE_NAME
                    ),
                    Statement.RETURN_GENERATED_KEYS
            );

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(this.information.getSnippet().getPublishedAt(), inputFormatter);

            statement.setInt(1, this.userId);
            statement.setString(2, this.channelId);
            statement.setString(3, this.videoId);
            statement.setTimestamp(4, Timestamp.valueOf(date.atStartOfDay()));
            statement.setInt(5, this.information.getStatistics().getViewCount());
            statement.setInt(6, this.information.getStatistics().getLikeCount());
            statement.setInt(7, this.information.getStatistics().getDislikeCount());
            statement.setInt(8, this.information.getStatistics().getCommentCount());
            statement.setTimestamp(9, new Timestamp(System.currentTimeMillis()));

            return statement;
        };
    }

}
