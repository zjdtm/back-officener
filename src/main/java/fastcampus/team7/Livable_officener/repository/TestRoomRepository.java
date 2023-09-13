package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.TestRoom;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
public class TestRoomRepository {

    private final Map<Long, TestRoom> tRooms;

    public TestRoomRepository() {
        tRooms = new ConcurrentHashMap<>();
        Stream.of(
                TestRoom.create("1번 채팅방"),
                TestRoom.create("2번 채팅방"),
                TestRoom.create("3번 채팅방"))
            .forEach(testRoom -> tRooms.put(testRoom.getId(),testRoom));
    }

    public Optional<TestRoom> findById(Long roomId) {
        return Optional.ofNullable(tRooms.get(roomId));
    }

    public void save(TestRoom testRoom) {
        tRooms.put(testRoom.getId(), testRoom);
    }
}
