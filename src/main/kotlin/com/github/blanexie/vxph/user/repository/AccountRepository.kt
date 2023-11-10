import com.github.blanexie.vxph.user.entity.Account
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface AccountRepository : CrudRepository<Account, Long>, QueryByExampleExecutor<Account> {
}