package models;

import java.io.Serializable;
import java.util.List;

public class Redirect2 implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long placementId;

    private String placementName;

    private String placementSize;

    private String date_begin;

    private String date_end;

    private boolean archived;

    private String date_last_modify;

    private List<RedirectTag2> redTagList;

    public Redirect2() {
    }

    public Redirect2(long placementId, String placementName, String placementSize, String date_begin, String date_end,
            boolean archived, String date_last_modify, List<RedirectTag2> redTagList) {
        this.placementId = placementId;
        this.placementName = placementName;
        this.placementSize = placementSize;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.archived = archived;
        this.date_last_modify = date_last_modify;
        this.redTagList = redTagList;
    }

    public long getPlacementId() {
        return placementId;
    }

    public void setPlacementId(long placementId) {
        this.placementId = placementId;
    }

    public String getPlacementName() {
        return placementName;
    }

    public void setPlacementName(String placementName) {
        this.placementName = placementName;
    }

    public String getPlacementSize() {
        return placementSize;
    }

    public void setPlacementSize(String placementSize) {
        this.placementSize = placementSize;
    }

    public String getDate_begin() {
        return date_begin;
    }

    public void setDate_begin(String date_begin) {
        this.date_begin = date_begin;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getDate_last_modify() {
        return date_last_modify;
    }

    public void setDate_last_modify(String date_last_modify) {
        this.date_last_modify = date_last_modify;
    }

    public List<RedirectTag2> getRedTagList() {
        return redTagList;
    }

    public void setRedTagList(List<RedirectTag2> redTagList) {
        this.redTagList = redTagList;
    }

}
