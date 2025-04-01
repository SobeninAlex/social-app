import glue.data.dataModule
import glue.domain.domainModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            dataModule,
            domainModule
        )
    }
}