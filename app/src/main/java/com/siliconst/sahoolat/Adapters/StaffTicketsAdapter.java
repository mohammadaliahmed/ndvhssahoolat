package com.siliconst.sahoolat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siliconst.sahoolat.Models.Ticket;
import com.siliconst.sahoolat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StaffTicketsAdapter extends RecyclerView.Adapter<StaffTicketsAdapter.ViewHolder> {
    Context context;
    List<Ticket> itemList;
    List<Ticket> arrayList;
    StaffTicketsAdapterCallbacks callbacks;

    public StaffTicketsAdapter(Context context, List<Ticket> itemList, StaffTicketsAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
        this.arrayList = new ArrayList<>(itemList);
    }

    public void updateList(List<Ticket> itemList) {
        this.itemList = itemList;
        arrayList.clear();
        arrayList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (Ticket item : arrayList) {
                if (item.getStatus().toLowerCase().contains(charText.toLowerCase())) {
                    itemList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_ticket_item_layout, parent, false);
        StaffTicketsAdapter.ViewHolder viewHolder = new StaffTicketsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ticket ticket = itemList.get(position);
        holder.title.setText(ticket.getSubject());
        holder.subtitle.setText("Ticket Status: " + ticket.getStatus());
        if (ticket.getStatus().equalsIgnoreCase("closed")) {
            holder.ticketStatus.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
        } else if (ticket.getStatus().equalsIgnoreCase("pending")) {
            holder.ticketStatus.setBackgroundColor(context.getResources().getColor(R.color.colorOriginalBlue));
            if (ticket.getAssignedTo() != null && ticket.getStaff() != null) {
                holder.subtitle.setText("Assigned to: " + ticket.getStaff().getName());
            } else {
                holder.subtitle.setText("Ticket Status: " + ticket.getStatus());
            }
        } else if (ticket.getStatus().equalsIgnoreCase("processing")) {
            holder.ticketStatus.setBackgroundColor(context.getResources().getColor(R.color.colorPurple));
            if (ticket.getAssignedTo() != null && ticket.getStaff() != null) {
                holder.subtitle.setText("Assigned to: " + ticket.getStaff().getName());
            } else {
                holder.subtitle.setText("Ticket Status: " + ticket.getStatus());
            }
        } else {
            holder.ticketStatus.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onViewTask(ticket);
//                Intent i = new Intent(context, ListOfRepliesToTicket.class);
//                i.putExtra("ticketId", ticket.getId());
//                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View ticketStatus;
        TextView title, subtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketStatus = itemView.findViewById(R.id.ticketStatus);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
        }
    }

    public interface StaffTicketsAdapterCallbacks {
        public void onViewTask(Ticket ticket);
    }
}
