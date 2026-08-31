package ralph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void constructor_nullInitial_createsEmptyList() {
        TaskList tl = new TaskList(null);
        assertEquals(0, tl.size());
    }

    @Test
    void getAll_unmodifiable_throwsOnAdd() {
        TaskList tl = new TaskList();
        tl.add(new Todo("a"));
        List<Task> all = tl.getAll();
        assertThrows(UnsupportedOperationException.class, () -> all.add(new Todo("b")));
    }

    @Test
    void addAndRemoveAndGet_multipleTasks_behaveAsExpected() {
        TaskList tl = new TaskList();
        Todo t1 = new Todo("first");
        Todo t2 = new Todo("second");
        tl.add(t1);
        tl.add(t2);
        assertEquals(2, tl.size());
        assertEquals(t1, tl.get(0));
        Task removed = tl.remove(0);
        assertEquals(t1, removed);
        assertEquals(1, tl.size());
        assertEquals(t2, tl.get(0));
    }
}
