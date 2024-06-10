package com.pucp.lab6_20175719.Dto;

import com.pucp.lab6_20175719.Entry.ListElementEntry;
import com.pucp.lab6_20175719.Out.ListElementOut;

import java.util.ArrayList;
import java.util.List;

public class DataDto {
    private static DataDto instance;

    private List<ListElementEntry> entryList;
    private List<ListElementOut> outList;
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public List<ListElementOut> getOutList() {
        return outList;
    }

    public void setOutList(List<ListElementOut> outList) {
        this.outList = outList;
    }
    private DataDto() {
        entryList = new ArrayList<>();
        outList = new ArrayList<>();
    }

    public static synchronized DataDto getInstance() {
        if (instance == null) {
            instance = new DataDto();
        }
        return instance;
    }

    public List<ListElementEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<ListElementEntry> entryList) {
        this.entryList = entryList;
    }
}
