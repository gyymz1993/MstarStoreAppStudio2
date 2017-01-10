//package cn.mstar.store.adapter;
//
//import java.util.HashMap;
//
//import android.content.Context;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import cn.mstar.store.R;
//import cn.mstar.store.app.Constants;
//import cn.mstar.store.fragments.ReductionTicketFragment;
//
///**
// * Created by Ultima on 7/11/2015.
// */
//public class ReductionTicketsManagementViewPagerAdapter extends FragmentPagerAdapter {
//
//    private InnerFragmentManager innerFragmentManager;
//    private String[] page_title;
//
//    public ReductionTicketsManagementViewPagerAdapter(FragmentManager fm, Context context) {
//        super(fm);
//        innerFragmentManager = InnerFragmentManager.getInstance();
//        page_title = context.getResources().getStringArray(R.array.reduction_tab_items);
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        if (innerFragmentManager == null)
//            innerFragmentManager = InnerFragmentManager.getInstance();
//        return innerFragmentManager.getFrag(position);
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        if (page_title == null) {
//            return "";
//        }
//        return page_title[position];
//    }
//
//    @Override
//    public int getCount() {
//        return Constants.GOODS_MENU_TAB_POSITION.length-1;
//    }
//
//
//    private static class InnerFragmentManager {
//
//        private HashMap<String, Fragment> frag_db;
//        private static InnerFragmentManager me;
//
//        public static InnerFragmentManager getInstance() {
//            if (me == null)
//                me = new InnerFragmentManager();
//            return me;
//        }
//
//        public Fragment getFrag(int position) {
//            String pos_ = (position + 1) + "";
//            if (frag_db == null)
//                frag_db = new HashMap<>();
//            if (frag_db.get(pos_) == null) {
//                switch (position + 1) {
//                    case Constants.ALL:
//                        frag_db.put(pos_, ReductionTicketFragment.getInstance(1));
//                        break;
//                    case Constants.NOT_USED:
//                        frag_db.put(pos_, ReductionTicketFragment.getInstance(2));
//                        break;
//                    case Constants.ALREADY_USED:
//                        frag_db.put(pos_, ReductionTicketFragment.getInstance(2));
//                        break;
//                    case Constants.EXPIRED:
//                        frag_db.put(pos_, ReductionTicketFragment.getInstance(2));
//                        break;
//                }
//            }
//            return frag_db.get(pos_);
//        }
//    }
//
//}
