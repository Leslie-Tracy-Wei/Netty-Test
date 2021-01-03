import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TestNetty {

    public static void main(String[] args) {

        String sql = "INSERT INTO `ec_item`.`store_qualification_mapping_info` (`id`, `store_id`, `qualification_manage_id`, `qualification_name`, `photo_url`, `enabled_flag`, `created_by`, `creation_date`, `updated_by`, `updation_date`, `etl_creation_date`, `etl_updation_date`) VALUES ('1344128165894737925', '1343500641133187075', '1', '食品流通许可证5', 'http://kyemall-1257092428.file.myqcloud.com/image/PBC04HK92j0ijcfzQcaN4Dvv0vOjjwO41609300033789.jpg', '1', '151971468643296777', '2020-12-30 11:48:22', '151971468643296777', '2020-12-30 11:48:22', NULL, NULL);\n";

        for (int i = 0; i < 266; i++) {
            System.out.println(sql);
        }


    }
}
