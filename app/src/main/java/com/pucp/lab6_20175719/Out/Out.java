package com.pucp.lab6_20175719.Out;

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

public class Out extends RecyclerView.Adapter<Out.OutView>{
    private List<ListElementOut> outList;
    private OnItemClickListener listener;

    public Out(List<ListElementOut> outList, OnItemClickListener listener) {
        this.outList = outList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OutView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_out, parent, false);
        return new OutView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutView holder, int position) {
        ListElementOut out = outList.get(position);
        holder.title.setText(out.getTitle());
        holder.mount.setText(String.valueOf(out.getMount()));
        holder.description.setText(out.getDescription());
        holder.date.setText(out.getDate());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(out));

        holder.deleteButton.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String path = "users/" + uid + "/" + "outs";
            FirebaseFirestore.getInstance().collection(path).document(out.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        outList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, outList.size());
                    })
                    .addOnFailureListener(e -> {
                    });
        });
    }


    public interface OnItemClickListener {
        void onItemClick(ListElementOut out);
    }

    @Override
    public int getItemCount() {
        return outList.size();
    }

    public static class OutView extends RecyclerView.ViewHolder {
        TextView title, mount, description, date;
        ImageButton deleteButton;

        public OutView(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            mount = itemView.findViewById(R.id.mount);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

}
