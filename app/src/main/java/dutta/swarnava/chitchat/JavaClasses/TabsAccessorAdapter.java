package dutta.swarnava.chitchat.JavaClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import dutta.swarnava.chitchat.Fragments.ChatsFragment;
import dutta.swarnava.chitchat.Fragments.FriendsFragment;
import dutta.swarnava.chitchat.Fragments.RequestsFragment;

public class TabsAccessorAdapter extends FragmentStatePagerAdapter {
    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return  chatsFragment;
//            case 1:
//                GroupsFragment groupsFragment=new GroupsFragment();
//                return  groupsFragment;
            case 1:
                FriendsFragment friendsFragment=new FriendsFragment();
                return  friendsFragment;
            case 2:
                RequestsFragment requestsFragment=new RequestsFragment();
                return  requestsFragment;
            default:return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return  "Chats";
//            case 1:
//                return  "Groups";
            case 1:
                return  "Friends";
            case 2:
                return  "Requests";

            default:return null;
        }
    }
}
