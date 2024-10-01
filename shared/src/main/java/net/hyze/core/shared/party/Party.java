package net.hyze.core.shared.party;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@ToString
public class Party {

    @Getter
    private final Integer id;

    @Getter
    @Setter
    private Integer leader;

    @Getter
    private List<Integer> members = Lists.newArrayList();

    public void setLeader(int userId) {
        this.leader = userId;
    }

    public void addMembers(Integer... userIds) {
        Collections.addAll(this.members, userIds);
    }

    public void removeMembers(Integer... userIds) {
        for(int userId : userIds) {
            this.members.remove(userId);
        }
    }

    public void clearMembers() {
        this.members.clear();
    }

}
