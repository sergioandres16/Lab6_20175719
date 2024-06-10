package com.pucp.lab6_20175719.Model;

import androidx.lifecycle.MutableLiveData;

import com.pucp.lab6_20175719.Entry.ListElementEntry;
import com.pucp.lab6_20175719.Out.ListElementOut;

import java.util.ArrayList;

public class ViewModel {

    private MutableLiveData<ArrayList<ListElementOut>> listOut = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ListElementEntry>> listEntry = new MutableLiveData<>();

    public MutableLiveData<ArrayList<ListElementOut>> getListOut() {
        return listOut;
    }

    public void setListOut(MutableLiveData<ArrayList<ListElementOut>> listOut) {
        this.listOut = listOut;
    }

    public MutableLiveData<ArrayList<ListElementEntry>> getListEntry() {
        return listEntry;
    }

    public void setListEntry(MutableLiveData<ArrayList<ListElementEntry>> listEntry) {
        this.listEntry = listEntry;
    }
}
