package giwi.org.cesitwitter.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;
import giwi.org.cesitwitter.R;
import giwi.org.cesitwitter.model.Message;

/**
 * Created by sca on 02/06/15.
 */
public class MessagesAdapter extends BaseAdapter {

    private final Context context;

    /**
     * Instantiates a new Messages adapter.
     *
     * @param ctx the ctx
     */
    public MessagesAdapter(Context ctx) {
        this.context = ctx;
    }

    List<Message> messages = new LinkedList<>();

    /**
     * Add message.
     *
     * @param messages the messages
     */
    public void addMessage(List<Message> messages) {
        this.messages = messages;
        this.notifyDataSetChanged();
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    @Override
    public int getCount() {
        if (messages == null) {
            return 0;
        }
        return messages.size();
    }

    /**
     * Gets item.
     *
     * @param position the position
     * @return the item
     */
    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    /**
     * Gets item id.
     *
     * @param position the position
     * @return the item id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Gets view.
     *
     * @param position    the position
     * @param convertView the convert view
     * @param parent      the parent
     * @return the view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_message, parent, false);
            vh = new ViewHolder();
            vh.username = (TextView) convertView.findViewById(R.id.msg_user);
            vh.message = (TextView) convertView.findViewById(R.id.msg_message);
            vh.date = (TextView) convertView.findViewById(R.id.msg_date);
            vh.img = (ImageView) convertView.findViewById(R.id.img);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.username.setText(getItem(position).getUsername());
        vh.message.setText(Html.fromHtml(getItem(position).getMsg()));
        vh.date.setText(getItem(position).getDate());
        if(getItem(position).getImageUrl() != null && getItem(position).getImageUrl().startsWith("http")) {
            Picasso.with(context).load(getItem(position).getImageUrl()).into(vh.img);
        }
        return convertView;
    }

    private class ViewHolder {
        TextView username;
        TextView message;
        TextView date;
        ImageView img;
    }
}
