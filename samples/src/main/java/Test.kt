import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
fun main() {
    runBlocking {
        channelFlow<Int> {
            var i = 0
            while (isActive && i<=4){
                delay(1.seconds)

                send(++i)
            }
            delay(998.milliseconds)
        }
//            .stateIn(this)
            .sample(1000.milliseconds)
            .collect {
            println("this is $it")
        }
    }
}