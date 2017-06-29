package org.zreo.cnbetareader.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;
import android.widget.ListView;
import android.widget.TextView;

import org.zreo.cnbetareader.Adapters.Tab_FragmentAdapter;
import org.zreo.cnbetareader.Model.CnInformation_Theme;
import org.zreo.cnbetareader.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab_Fragment2 extends Fragment {
    private View view;
    private ListView theme_mayattention_listview;
    private CnInformation_Theme  cnInformation_theme;
    List<CnInformation_Theme> list = new ArrayList<CnInformation_Theme>();
    Tab_FragmentAdapter tab_fragmentAdapter;
    private View loadMoreView;     //加载更多布局
    private TextView loadMoreText;    //加载提示文本
    TextView newType;
    public interface click{
        public void buttonClick(Integer id);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_theme_mayattention, container, false); //获取布局
        jiaData();//初始化
        theme_mayattention_listview= (ListView)
                view.findViewById(R.id.theme_mayattention_listview);//找到theme_mayattention_listview控件
        newType= (TextView) view.findViewById(R.id.tv_type2);//找到文本类型控件
        theme_mayattention_listview.addFooterView(loadMoreView);//添加更多视图
        tab_fragmentAdapter=new Tab_FragmentAdapter(getActivity(),R.layout.itv_tab2,list);//创建适配器
        theme_mayattention_listview.setAdapter(tab_fragmentAdapter);//添加适配器
        String newtype=cnInformation_theme.getThemetype();//获取主题类型
        newType.setText(newtype);//设置主题类型
        return view;
    }

public void jiaData(){
    String[] type=new String[]{"A","B","C","D","F","G","V",};
    String[] title=new String[]{"ADsee","AMD","AloAng","Adobe","ADC","ABC","AE"};
    String firstWord="A";
    for(int i=0;i<7;i++){
        cnInformation_theme=new CnInformation_Theme();
        cnInformation_theme.setThemetype(type[0]);
        cnInformation_theme.setContent(title[i]);
        cnInformation_theme.setFirstWord(firstWord);
        list.add(cnInformation_theme);
    }
    loadMoreView = getActivity().getLayoutInflater().inflate(R.layout.load_more, null);
    loadMoreText = (TextView) loadMoreView.findViewById(R.id.load_more);
    loadMoreText.setBackgroundColor(getResources().getColor(R.color.gray));
    loadMoreText.setText("-- The End --");


}
}
