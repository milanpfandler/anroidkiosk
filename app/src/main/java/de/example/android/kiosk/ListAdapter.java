package de.example.android.kiosk;

    import java.util.List;

    import android.annotation.SuppressLint;
    import android.app.Activity;
    import android.content.pm.PackageInfo;
    import android.content.pm.PackageManager;
    import android.graphics.drawable.Drawable;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.View.OnClickListener;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.CheckBox;
    import android.widget.TextView;

    public class ListAdapter extends BaseAdapter{

        List <PackageInfo> packageList;
        Activity context;
        PackageManager packageManager;
        boolean[] itemChecked;

        public ListAdapter(Activity context, List <PackageInfo> packageList,
                           PackageManager packageManager) {
            super();
            this.context = context;
            this.packageList = packageList;
            this.packageManager = packageManager;
            itemChecked = new boolean[packageList.size()];
        }

        private class ViewHolder {
            TextView apkName;
            TextView pckName;
            CheckBox ck1;
        }

        public int getCount() {
            return packageList.size();
        }

        public Object getItem(int position) {
            return packageList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            LayoutInflater inflater = context.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();

                holder.apkName = (TextView) convertView
                        .findViewById(R.id.textView1);
                holder.pckName = (TextView) convertView
                        .findViewById(R.id.textView0);
                holder.ck1 = (CheckBox) convertView
                        .findViewById(R.id.checkBox1);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            PackageInfo packageInfo = (PackageInfo) getItem(position);

            Drawable appIcon = packageManager
                    .getApplicationIcon(packageInfo.applicationInfo);
            String appName = packageManager.getApplicationLabel(
                    packageInfo.applicationInfo).toString();

            String Package = packageInfo.packageName;

                    appIcon.setBounds(0, 0, 40, 40);

            holder.apkName.setCompoundDrawables(appIcon, null, null, null);
            holder.apkName.setCompoundDrawablePadding(15);
            holder.apkName.setText(appName);
            holder.pckName.setText(Package);

            if (itemChecked[position])
                holder.ck1.setChecked(true);
            else
                holder.ck1.setChecked(false);

            holder.ck1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemChecked[position] = holder.ck1.isChecked();
                }
            });

            return convertView;

        }

    }

