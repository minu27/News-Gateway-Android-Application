package edu.iit.itmd555.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ArticleFragment extends Fragment {

    private static final String TAG = "ArticleFragment";

    private Article articleContainer;

    public ArticleFragment() {

    }

    public ArticleFragment(Article article) {
        this.articleContainer = article;
    }

    static ArticleFragment newInstance(Article article, int index, int max) {
        ArticleFragment articleFragment = new ArticleFragment(article);
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("ARTICLE_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        articleFragment.setArguments(bdl);
        return articleFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Article article = (Article) args.getSerializable("ARTICLE_DATA");
            if (article == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            TextView headline = fragment_layout.findViewById(R.id.headline);
            if(article.getTitle() == null || article.getTitle().equals("null"))
                headline.setText("");
            else
                headline.setText(article.getTitle());
            headline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(articleContainer.getUrl())));
                }
            });

            TextView date = fragment_layout.findViewById(R.id.date);
            String rawDate = article.getPublishedAt();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date dateObj = null;
            try {
                dateObj = format.parse(rawDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            date.setText(new SimpleDateFormat("E MMM dd, hh:mm aa").format(dateObj));

            TextView author = fragment_layout.findViewById(R.id.author);
            if(article.getAuthor() == null || article.getAuthor().equals("null"))
                author.setText("");
            else
                author.setText(article.getAuthor());

            ImageView mainPic = fragment_layout.findViewById(R.id.mainPic);
            Picasso picasso = new Picasso.Builder(this.getContext()).build();
            if (article.getUrlToImage() == null) {
                mainPic.setVisibility(View.INVISIBLE);
            } else {
                picasso.load(article.getUrlToImage())
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(mainPic);
            }

            mainPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(articleContainer.getUrl())));
                }
            });

            TextView description = fragment_layout.findViewById(R.id.description);
            if(article.getDescription() == null || article.getDescription().equals("null"))
                description.setText("");
            else
                description.setText(Html.fromHtml(article.getDescription()));

            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(articleContainer.getUrl())));
                }
            });

            TextView pageNum = fragment_layout.findViewById(R.id.index);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            return fragment_layout;
        } else {
            return null;
        }
    }

}
