package jiguang.chat.activity.historyfile.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import jiguang.chat.R;
import jiguang.chat.activity.historyfile.fragment.OtherFileFragment;
import jiguang.chat.adapter.StickyListHeadersAdapter;
import jiguang.chat.entity.FileItem;
import jiguang.chat.entity.SelectedHistoryFileListener;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.ViewHolder;
import jiguang.chat.view.MyImageView;

/**
 * Created by ${chenyn} on 2017/8/29.
 */

public class OtherFileAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private List<FileItem> mList;
    private LayoutInflater mInflater;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private SelectedHistoryFileListener mListener;
    private Context mContext;
    public OtherFileAdapter(OtherFileFragment fragment, List<FileItem> documents) {
        this.mList = documents;
        this.mContext = fragment.getContext();
        this.mInflater = LayoutInflater.from(fragment.getActivity());
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.section_tv);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.text.setText(mList.get(position).getDate());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FileItem item = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_file_document, null);
        }
        final CheckBox checkBox = ViewHolder.get(convertView, R.id.document_cb);
        TextView title = ViewHolder.get(convertView, R.id.document_title);
        TextView size = ViewHolder.get(convertView, R.id.document_size);
        TextView date = ViewHolder.get(convertView, R.id.document_date);
        MyImageView imageView = ViewHolder.get(convertView, R.id.document_iv);
        LinearLayout ll_document = ViewHolder.get(convertView, R.id.document_item_ll);

        if ( SharePreferenceManager.getShowCheck()) {
            checkBox.setVisibility(View.VISIBLE);
        }else {
            checkBox.setVisibility(View.GONE);
        }

        title.setText(item.getFileName());
        size.setText(item.getFileSize());
        date.setText(item.getFromeName() + "  " + item.getDate());
        imageView.setImageResource(R.drawable.jmui_other);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    checkBox.setChecked(true);
                    mSelectMap.put(position, true);
                    mListener.onSelected(item.getMsgId(), item.getMsgId());
                }else {
                    mSelectMap.delete(position);
                    mListener.onUnselected(item.getMsgId(), item.getMsgId());
                }
            }
        });

        ll_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseDocument(item.getFileName(), item.getFilePath());
            }
        });

        return convertView;
    }

    private void browseDocument(String fileName, String path) {
        try {
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mime = mimeTypeMap.getMimeTypeFromExtension(ext);
            File file = new File(path);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), mime);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.file_not_support_hint, Toast.LENGTH_SHORT).show();
        }
    }

    public void setUpdateListener(SelectedHistoryFileListener listener) {
        this.mListener = listener;
    }

    private static class HeaderViewHolder {
        TextView text;
    }
}