package com.intexh.bidong.me.model;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import com.intexh.bidong.R;
import com.intexh.bidong.userentity.ModelWorkItemEntity;
import com.intexh.bidong.utils.ImageUtils;
import com.intexh.bidong.utils.UserUtils;

/**
 * Created by shenxin on 15/12/28.
 */

public class ModelPicsAdapter extends RecyclerView.Adapter<ModelPicsAdapter.ViewHolder> {

    public interface OnModelViewClickListener {
        public void onClickCupSize();

        public void onClickServiceInfo();

        public void onClickWorks();
    }

    private List<ModelWorkItemEntity> datas = null;
    private OnModelViewClickListener adapterListener = null;
    private String xiongwei;
    private String yaowei;
    private String tunwei;
    private String shoesize;
    private String cupSize;
    private String country;
    private String serviceInfo;
    private boolean isShowldShowHeader = true;

    public void setIsShowldShowHeader(boolean isShowldShowHeader) {
        this.isShowldShowHeader = isShowldShowHeader;
    }

    public void setAdapterListener(OnModelViewClickListener adapterListener) {
        this.adapterListener = adapterListener;
    }

    public String getXiongwei() {
        return xiongwei;
    }

    public void setXiongwei(String xiongwei) {
        this.xiongwei = xiongwei;
    }

    public String getYaowei() {
        return yaowei;
    }

    public void setYaowei(String yaowei) {
        this.yaowei = yaowei;
    }

    public String getTunwei() {
        return tunwei;
    }

    public void setTunwei(String tunwei) {
        this.tunwei = tunwei;
    }

    public String getShoesize() {
        return shoesize;
    }

    public void setShoesize(String shoesize) {
        this.shoesize = shoesize;
    }

    public String getCupSize() {
        return cupSize;
    }

    public void setCupSize(String cupSize) {
        this.cupSize = cupSize;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public void setDatas(List<ModelWorkItemEntity> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(isShowldShowHeader){
            if (0 == position) {
                return 0;
            } else {
                return 1;
            }
        }else{
            return 1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.image_modelpics_pic)
        ImageView modelPicView;
        @ViewInject(R.id.edit_modelinfo_country)
        private EditText countryInput;
        @ViewInject(R.id.edit_modelinfo_shoesize)
        private EditText shoeSizeInput;
        @ViewInject(R.id.edit_modelinfo_tunwei)
        private EditText tunweiInput;
        @ViewInject(R.id.edit_modelinfo_xiongwei)
        private EditText xiongweiInput;
        @ViewInject(R.id.edit_modelinfo_yaowei)
        private EditText yaoweiInput;
        @ViewInject(R.id.txt_modelinfo_cupsize)
        private TextView cupsizeTxt;
        @ViewInject(R.id.txt_modelinfo_serviceinfo)
        private TextView serviceInfoTxt;
        @ViewInject(R.id.layout_modelinfo_works)
        private View worksView;
        @ViewInject(R.id.layout_modelinfo_serviceinfo)
        private View serviceInfoLayout;
        @ViewInject(R.id.layout_modelinfo_cupsize)
        private View cupsizeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ViewUtils.inject(this, itemView);
        }
    }

    private ModelWorkItemEntity getItem(int index) {
        if (null == datas) {
            return null;
        } else {
            return datas.get(index);
        }
    }

    @Override
    public ModelPicsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (1 == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem_modelpics, null);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_modelinfoheader, null);
            ViewHolder holder = new ViewHolder(view);
            if (1 == UserUtils.getUserInfo().getUser().getGender()) {
                holder.cupsizeLayout.setVisibility(View.VISIBLE);
            } else {
                holder.cupsizeLayout.setVisibility(View.GONE);
            }
            holder.xiongweiInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().equals(xiongwei)) {

                    } else {
                        xiongwei = editable.toString();
                    }
                }
            });
            holder.yaoweiInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().equals(yaowei)) {

                    } else {
                        yaowei = editable.toString();
                    }
                }
            });
            holder.tunweiInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().equals(tunwei)) {

                    } else {
                        tunwei = editable.toString();
                    }
                }
            });
            holder.shoeSizeInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().equals(shoesize)) {

                    } else {
                        shoesize = editable.toString();
                    }
                }
            });
            holder.countryInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().equals(country)) {

                    } else {
                        country = editable.toString();
                    }
                }
            });
            holder.cupsizeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != adapterListener) {
                        adapterListener.onClickCupSize();
                    }
                }
            });
            holder.serviceInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != adapterListener) {
                        adapterListener.onClickServiceInfo();
                    }
                }
            });
            holder.worksView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != adapterListener) {
                        adapterListener.onClickWorks();
                    }
                }
            });
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(ModelPicsAdapter.ViewHolder holder, int position) {
        if(isShowldShowHeader){
            if (0 == position) {
                holder.xiongweiInput.setText(xiongwei);
                holder.yaoweiInput.setText(yaowei);
                holder.tunweiInput.setText(tunwei);
                holder.shoeSizeInput.setText(shoesize);
                holder.cupsizeTxt.setText(cupSize);
                holder.countryInput.setText(country);
                holder.serviceInfoTxt.setText(serviceInfo);
            } else {
                ModelWorkItemEntity entity = getItem(position - 1);
                ImageUtils.loadWorksImage(entity.getUri(), holder.modelPicView, 1.0f);
            }
        }else{
            ModelWorkItemEntity entity = getItem(position);
            ImageUtils.loadWorksImage(entity.getUri(), holder.modelPicView, 1.0f);
        }
    }

    @Override
    public int getItemCount() {
        if(isShowldShowHeader){
            if (null != datas) {
                return datas.size() + 1;
            }
            return 1;
        }else{
            if (null != datas) {
                return datas.size();
            }
            return 0;
        }

    }
}
