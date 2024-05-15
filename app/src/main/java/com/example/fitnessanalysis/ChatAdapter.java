package com.example.fitnessanalysis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.TextPart;


import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_MODEL = 2;
    private List<Content> chatHistory;

    public ChatAdapter(List<Content> chatHistory) {
        this.chatHistory = chatHistory;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater;
        inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_USER:
                view = inflater.inflate(R.layout.item_user_message, parent, false);
                return new UserMessageViewHolder(view);
            case VIEW_TYPE_MODEL:
                view = inflater.inflate(R.layout.item_model_message, parent, false);
                return new ModelMessageViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Content content = chatHistory.get(position);
//        switch (holder.getItemViewType()) {
//            case VIEW_TYPE_USER:
//                ((UserMessageViewHolder) holder).bind(content);
//                break;
//            case VIEW_TYPE_MODEL:
//                ((ModelMessageViewHolder) holder).bind(content);
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid view type");
//        }
        if (position == 0){
            return;
        } else {
            Content content = chatHistory.get(position);
            if (holder instanceof UserMessageViewHolder) {
                ((UserMessageViewHolder) holder).bind(content);
            } else if (holder instanceof ModelMessageViewHolder) {
                ((ModelMessageViewHolder) holder).bind(content);
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatHistory.size();
    }

    @Override
    public int getItemViewType(int position) {
        Content content = chatHistory.get(position);
        return content.getRole().equals("user") ? VIEW_TYPE_USER : VIEW_TYPE_MODEL;
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.user_message_text);
        }

        void bind(Content content) {
            // Assuming you have a method to extract the text from the Content object
            String text = getTextFromContent(content);
            messageTextView.setText(text);
        }

        private String getTextFromContent(Content content) {
            List<Part> parts = content.getParts();
            for (Part part : parts) {
                if (part instanceof TextPart) {
                    return ((TextPart) part).getText();
                }
            }
            return ""; // Return empty string if no text part found
        }
    }

    static class ModelMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        ModelMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.model_message_text);
        }

        void bind(Content content) {
            // Assuming you have a method to extract the text from the Content object
            String text = getTextFromContent(content);
            messageTextView.setText(text);
        }

        private String getTextFromContent(Content content) {
            List<Part> parts = content.getParts();
            for (Part part : parts) {
                if (part instanceof TextPart) {
                    return ((TextPart) part).getText();
                }
            }
            return ""; // Return empty string if no text part found
        }
    }
}
