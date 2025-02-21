package com.hz6826.clockin.sql;

import com.hz6826.clockin.ClockIn;
import com.hz6826.clockin.init.DatabaseConn;
import io.ebean.Transaction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

import io.ebean.Database;

@Environment(EnvType.SERVER)
public class DatabaseManager {
    public DatabaseManager(){

    }
    String SERVER_UUID = "00000000-0000-0000-0000-000000000000";

    // User methods
    /**
     * 这两个方法最好分开
     * 嗯嗯，
     * 大部分的操作用Database的查询功能一键就能查询了，所以这里只需要包装一些简单的方法就行了
     * 对了我想把User和EconomyAccount合并下，以后UserWithAccountAbstract改成User吧
     * 话说一个玩家能有多个EconomyAccount吗，好像没必要实现那玩意吧，倒是，但是感觉搞复杂了
     * 啊,可以吧多办几张银行卡
     * 是的,但是改完后user与银行账户强耦合了
     * 倒确实是，emm，我想想，先合上吧以后要拆开再说
     *
     * **/

    {
        Database db = DatabaseConn.bootstrap();
        User nu1l = new User(SERVER_UUID, "nu1l", 0, 0, 0);
        nu1l.save();
        db.save(nu1l);

        // 查这么写就行了
        try(Transaction t = db.currentTransaction()){

        } catch (Exception e) {

        }
        db.createQuery(User.class).where().eq("uuid", SERVER_UUID).findOneOrEmpty()
                .orElseThrow()
                .setBalance(100)
                .save();

        //我看看支不支持链式调用，awa，忘了这个是我们自己写的了，要是我们自己setter设置为返回自身就支持了他怎么设置事务,不然高并发下会出问题
        //




    }


}
