package seedu.address.model.tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Constructs a set of {@code Tag}s that are predefined by the user.
 */
public class TagList {
    private Set<Tag> tags;

    public TagList() {
        tags = new HashSet<>();
    }

    /**
     * Adds a new tag if it is not already present.
     *
     * @param tag The tag to add.
     * @return true if the tag was added, false if it already exists.
     */
    public boolean addTag(Tag tag) {
        return tags.add(tag);
    }

    /**
     * Sets the TagList based on a list from Storage.
     *
     * @param tags The tags to add.
     */
    public void setTags(List<Tag> tags) {
        this.tags = new HashSet<>(tags);
    }

    /**
     * Returns true if the tag exists in the list.
     */
    public boolean contains(Tag tag) {
        return tags.contains(tag);
    }

    /**
     * Returns the backing set as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Tag> asUnmodifiableObservableList() {
        return FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(tags));
    }
    @Override
    public String toString() {
        return String.join(", ", tags.stream().map(tag -> "[" + tag.getTagName() + "]").toList());
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TagList)) {
            return false;
        }
        TagList otherTagList = (TagList) other;
        return tags.equals(otherTagList.tags);
    }
}
