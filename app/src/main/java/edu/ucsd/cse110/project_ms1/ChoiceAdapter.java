package edu.ucsd.cse110.project_ms1;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ChoiceAdapter extends RecyclerView.Adapter<edu.ucsd.cse110.project_ms1.ChoiceAdapter.ViewHolder>{
    private List<String> choiceStringList = Collections.emptyList();
    public void setChoiceStringList(List<String> new_choice_string_list) {
        this.choiceStringList.clear();
        this.choiceStringList = new_choice_string_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_direction, parent, false);
        return new ChoiceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceAdapter.ViewHolder holder, int position) {
        holder.setChoice(choiceStringList.get(position));
    }

    @Override
    public int getItemCount() {
        return choiceStringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView choiceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.choiceTextView = itemView.findViewById(R.id.a_direction_item);
        }

        public void setChoice(String currentChoice) {
            this.choiceTextView.setText(currentChoice);
        }
    }
}
