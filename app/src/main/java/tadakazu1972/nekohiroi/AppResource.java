package tadakazu1972.nekohiroi;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tadakazu on 2015/09/22.
 */
public class AppResource {
    private Context m_Context;
    //  コンストラクタ
    public AppResource( Context context )
    {
        m_Context = context;
    }

    //  読み込み
    public InputStream Load( String filePath )
    {
        InputStream inputStream = null;

        try
        {
            inputStream = m_Context.getAssets().open( filePath );
        }
        catch( IOException e )
        {
            e.printStackTrace();
            return null;
        }
        return inputStream;
    }
}
