package com.pucp.lab6_20175719.Entry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pucp.lab6_20175719.R;

import java.util.List;

public class Entry extends RecyclerView.Adapter<Entry.EntryView>{
    private List<ListElementEntry> EntryList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ListElementEntry item);
    }

    public Entry(List<ListElementEntry> EntryList, OnItemClickListener listener) {
        this.EntryList = EntryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EntryView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_entry, parent, false);
        return new EntryView(view);
    }


    @Override
    public int getItemCount() {
        return EntryList.size();
    }

    public static class EntryView extends RecyclerView.ViewHolder {
        TextView title, mount, description, date;
        ImageButton deleteButton;

        public EntryView(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            mount = itemView.findViewById(R.id.mount);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EntryView holder, int position) {
        ListElementEntry entry = EntryList.get(position);
        holder.title.setText(entry.getTitle());
        holder.mount.setText(String.valueOf(entry.getMount()));
        holder.description.setText(entry.getDescription());
        holder.date.setText(entry.getDate());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(entry));

        holder.deleteButton.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String path = "users/" + uid + "/" + "Entry";
            FirebaseFirestore.getInstance().collection(path).document(entry.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        EntryList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, EntryList.size());
                    })
                    .addOnFailureListener(e -> {
                    });
        });
    }
}
