package ru.scrait.contactlesspayment.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.scrait.contactlesspayment.R;
import ru.scrait.contactlesspayment.RecyclerHistoryInterface;

public class RecyclerViewHistoryAdapter extends RecyclerView.Adapter<RecyclerViewHistoryAdapter.ViewHolder> {
    private Context context;

    private List<String> listDates;
    private List<Integer> listSums;
    private List<Integer> listAmounts;

    // data is passed into the constructor

    private final RecyclerHistoryInterface recyclerHistoryInterface;
    public RecyclerViewHistoryAdapter(Context context,
                                      List<String> dates,
                                      List<Integer> sums,
                                      List<Integer> amounts,
                                      RecyclerHistoryInterface recyclerHistoryInterface) {

        this.context = context;

        this.listDates = dates;
        this.listSums = sums;
        this.listAmounts = amounts;
        this.recyclerHistoryInterface = recyclerHistoryInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view, recyclerHistoryInterface);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHistoryAdapter.ViewHolder holder, int position) {
        String date = listDates.get(position);
        int sum = listSums.get(position);
        int amount = listAmounts.get(position);

        holder.tvDate.setText(date);
        holder.tvSum.setText(sum + " rub.");
        holder.tvAmount.setText(amount + " pcs.");

    }

    @Override
    public int getItemCount() {
        return listDates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvDate;
        TextView tvAmount;
        TextView tvSum;
        public ViewHolder(@NonNull View itemView, RecyclerHistoryInterface recyclerHistoryInterface) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvRecyclerViewDate);
            tvAmount = itemView.findViewById(R.id.tvRecyclerViewAmount);
            tvSum = itemView.findViewById(R.id.tvRecyclerViewSum);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerHistoryInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            recyclerHistoryInterface.OnItemClick(pos);
                        }
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
