NIO和零拷贝
在java中常用的零拷贝为mmap(内存映射) 和 sendFlie
mmap 适合小数据量读写 sendFile 适合大文件传输

传统的io数据读写 
用户态和内核态的切换 
经过了4次copy 3次切换
DMA：direct momory access 直接内存拷贝 不使用cpu

mmap优化：将文件映射到内核缓冲区，同时用户空间可以共享内核空间的数据
拷贝次数为3次 切换3次 减少了一次拷贝

linux 2.1 sendFile优化:数据根本不经过用户态 直接从内核缓冲区进入到socket buffer
拷贝次数为3次 切换为2次

linux 2.4 避免了从内核缓冲区拷贝到Socket buffer 的操作 直接拷贝到协议栈，从而减少了数据拷贝 
其实有一次的cpu copy 但是数据很少 比如length offset等信息可以忽略
拷贝次数为2次 
所为的零拷贝 是没有CPU copy 是从操作系统的角度 

hard driver --- DMA copy -> kernel buffer ---- cpu copy ----> socket buffer ---- DMA copy -> protocol engine

零拷贝减少了上下文切换，更少的数据复制



AIO ： NIO2.0 异步不阻塞 

同步阻塞：到理发店理发，就一直等理发师，直到轮到自己理发
同步非阻塞：到理发店理发，发现前面有人，给理发师说一声，先做其他事，在回来
异步非阻塞：给理发师打电话，让理发师上门服务，自己做其他事情，等待上门
JAVA NIO 就是个Reactor 



Netty：异步的 事件驱动 网络应用框架 快速开发高性能的server和client
原生NIO的问题：类库复杂，开发工作量和难度非常大
JAVA NIO的bug ： Epollbug 导致selector空轮训，导致CPU100% 

优点：设计优雅，适合阻塞，非阻塞socket ；
使用方便 没有其他依赖项 
高性能 吞吐量高，资源消耗少
安全 完整的ssl、tls starttls

netty3.x netty4.x netty5.x 

线程模型：
传统阻塞I/O服务模型
    一个线程对应一个连接
    采用阻塞io模式获取输入的数据
    每个连接都需要独立的线程完成数据的输入，业务处理，数据返回
    问题：
        并发数很大，创建大量线程，占用大量系统资源 
        如果当前线程暂时没有数据可读，会一直阻塞在read操作上造成线程资源浪费
Reactor模式(反应器模式 、 分发者模式(dispatcher) 、通知者模式(notifier))：
    基于I/O复用模型：多个连接公用一个阻塞对象，应用程序只需要在一个阻塞对象等待，无须等待所有连接
    基于线程池复用线程资源：不必再为每个连接创建线程，将连接完成后的业务处理任务分配给线程进行处理，一个线程可以处理多个连接的业务
    核心组成:
        一个分发器 (接线员)
        对应的处理器(handler) (实际工作人员)
    实现模式:
        单Reactor单线程
            优点:
                模型简单，没有多线程，进程通信，竞争的问题，全部都在一个线程完成
            缺点:
                只有一个线程，无法完全发挥多核的性能
                reactor所在的线程跟handler所在的线程是同一个线程
                高并发下，会出现瓶颈；
                可靠性问题，线程意外终止，或者死循环会导致整个系统通信模块不可用 
            使用场景:
                redis在业务处理的时间为O（1）的情况
        单Reactor多线程
            优点:
                充分利用多核CPU的处理能力
            缺点:
                多线程会出现数据共享和访问比较麻烦，
                reactor处理所有的事件的监听和响应，在单线程运行，在高并发容易出现性能瓶颈

        主从Reactor多线程 netty基于此模型 并且做了改进 
            优点:
                父线程与子线程的数据交互简单职责明确，父线程只需要接收新连接，子线程完成后续的业务处理
                Reactor主线程只需要把新连接传给子线程，子线程无须返回数据
            缺点:
                编程复杂度高
            使用场景:
                Nginx主从 memcached netty
        三种模式：
            前台接待员和服务员是同一个人
            一个前台，多个服务员，接待员只负责接待
            多个前台，多个服务员，

Netty模型:
	BossGroup:
	WorkerGroup:
	netty流程 
		基本流程:
			1)BossGroup线程维护selector 只关注Accecpt
			2)当接收到Accecpt,获取到对应的ScoketChannel,封装成NIOSocketChannel并注册到WorkerGroup（事件循环），并进行维护
			3)当worker线程监听到Selector中通道发生自己感兴趣的事件后，就进行处理(就由handler，此时handler已经加入到通道中)
		详细流程:
			1)Netty抽象出两组线程池 BossGroup只关注接收客户端的连接，WorkerGroup专门负责网络的读写
			2)BossGroup和WorkerGroup类型都是NIOEventLoopGroup
			3)NIOEventLoopGroup相当于一个事件循环组，其中包含了多个事件循环，每个事件循环就是NIOEventLoop
			4)NIOEventLoop表示一个不断循环的执行处理任务的线程，每个NIOEventLoop都有一个Selector,用于监听绑定在其中的socket的网络通讯
			5)NIOEventLoopGroup可以有多个线程,既可以含有多个NIOEventLoop
			6)每个Boss NIOEventLoop 循环执行有三步
				1.轮训accecpt事件
				2.处理accecpt事件，与client创立连接，生成NioSocketChannel，并将其注册到某个worker NIOEventLoop 上的selector
				3.处理任务队列的任务 即runAllTasks
			7)每个Worker NIOEventLoop 循环执行有三步
				1.轮训read write事件
				2.处理i/o事件 即read write事件，在对应NioSocketChannel处理
				3.处理任务队列的任务，即runAllTasks
			8)每个worker NIOEventLoop 处理业务时，会使用到pipeline(管道),pipeline中包含了channel，所以通过pipeline可以获取到对应通道，管道中维护了很多的处理器
			
			
	netty默认生成的NIOeventLoop为cpu * 2
	pipeline 底层是一个双向链表
	
	任务队列的task有三种典型使用场景
	1) 用户程序自定义的普通任务 ctx.channel.eventLoop.().execute(new Runnable());
	2) 用户自定义定时任务 ctx.channel.eventLoop.schedule(new Runnable());
	3) 非当前Reactor线程调用Channel的各种方法 
	
	
	总结：
	
	netty抽象出两个线程池 BossGroup专门负责接收客户端连接，WorkerGroup专门负责网络的读写
	NIOEventLoop表示一个不断循环的执行处理任务的线程，每个NIOEventLoop都有一个Selector
	NIOEventLoop内部采用串行化设计，从消息的读取->解码->处理->编码->发送，始终由IO线程NIOEventLoop负责
	
	NIOEventLoopGroup下包含多个NioEventLoop
	每个NIOEventLoop都有一个Selector，一个taskQueue
	每个NIOEventLoop的Selector上可以注册监听多个NioChannel
	每个NioChannel只会绑定在唯一的NioEventLoop上
	每个NioChannel都绑定有一个自己的channelPipeLine
	
	
	异步模型:
		当一个异步过程调用发出后，调用者不能立即得到结果 而是通过Future-listener机制
		Netty的I/O操作都是异步的，包含bind write，connect等操作都是返回一个ChannelFuture

		ChannelFuture ->The result of an asynchronous
		
		netty就是建立在future---callback之上的
		
		
		Future：
			1)表示异步的执行结果，可以通过它提供的方法来检测执行是否完成，比如检索计算等
			2) ChannelFuture 是一个接口 我们可以添加监听器，当监听的事件发生时，就会通知到监听器
		
		链式操作处理:
			读取数据 -> 数据处理(编码) -> 数据传输 -> 数据处理(解码)->显示数据
			每一个都对应一个handler
			在handler处理中，可以使用callback或者future实现异步
		Future-listener机制:
			当Future对象刚刚创建时，处于非完成状态
			
			isSuccess():判断已完成的当前操作是否成功
			isCancelled():判断已完成的当前操作失败的原因
			isDone():判断当前操作是否完成
			isCancellable():是否可以被取消 if and only if the operation can be cancelled
			getCause:获取已完成的当前操作失败的原因
			addListener():来注册监听器
	
	Pipeline/ChannelPipeline:
		ChannelPipeline是一个Handler的集合，他负责处理和拦截inbound或者outbound的时间和操作，相当于贯穿Netty的链 
		(ChannelPipeline是保存ChannelHandler的list，用于处理或者拦截Channel的入站事件和出站操作)
		ChannelPipeline实现了一种高级形式的拦截过滤器模式，使得用户完全可以控制事件的处理方式
		
		Channel都有一个ChannelPipeline ChannelPipeline中又维护了一个由ChannelHandlerContext组成的双向链表 ChannelHandlerContext又关联着一个ChannelHandler
		
	ChannelHandlerContext:
		1) 保存了Channel所有的上下文信息 实际的类型是DefaultChannelHandlerContext 
		2) 包含一个具体的事件处理器ChannelHandler 也绑定对应的pipeline和channel的信息
		3) 常用方法:
			close(): 关闭通道
			flush(): 刷新
			writeAndFlush(): 将数据写入到ChannelPipeline中当前ChannelHandler的下一个ChannelHandler开始处理
			
	ChannelOption:
		SO_BACKLOG:用来初始化服务器可连接队列大小，服务器处理客户端来的请求是顺序的，所以同一时间只能处理一个客户端，多个客户端来的时候，将不能不能处理的客户端放在队列当中
		SO_KEEPALIVE:一直保持连接状态
	
	EventLoopGroup:
		NioEventLoopGroup是一组EventLoopGroup抽象 每个EventLoopGroup维护Selector
		
	Unpooled:
		copiedBuffer():返回一个ByteBuf
		Netty提供一个专门用来操作缓冲区的工具类
		ByteBuf不需要使用flip()进行翻转
		因为底层维护了readerIndex writerIndex
		readerIndex writerIndex capacity 将buffer分为3端 
		0---readerIndex 已经读取的区域
		readerIndex --- writerIndex 可读区域
		writerIndex --- capacity 可写区域

	Netty心跳检测机制:
		当服务器超过3秒没有读时，就提示读空闲
		当服务器超过5秒没有写操作时，就提示写空闲
		当服务器超过7秒没有读或者写操作时，就提示读写空闲
	WebSocket编程:
	    http协议是无状态的，每次都会重新连接一次
	    基于webSocket的长连接实现全双工通讯

    编码、解码:
        C端-> 业务数据 --- 编码 ->  服务端 ----解码 ---- 对应的业务数据
        codec 包含 decoder encoder
        netty自身提供codec
            StringEncoder:对字符串数据进行编码
            ObjectEncoder: 底层还是使用java序列化的技术，无法实现跨语言 序列化后体积太大 性能太低
            改进:
                Protobuf: Google
                    做数据存储 /RPC【remote procedure call】 远程过程调用
                    http + json -》 tcp + protobuf
                    以message来管理数据的
                    支持跨平台 跨语言 支持巨大多数语言
                    高性能 高可靠性
                    .proto文件 -> protoc.exe 生产java文件
                    XX.proto -> protoc.exe -> xx.java -> ProtoBufEncoder -> 二进制流 -> ProtoBufDecoder -> xx.java



    netty入站出站规则:
        C -> S :出站 Inbound 编码
        S -> C :入站 Outbound 解码
        结论:
            不论解码器handler还是编码器handler，即接收的消息类型必须与待处理的消息类型一致，否则该handler不会被执行
            在解码器进行数据解码时，需要判断缓存区buffer的数据是否足够，否则接收到的数据结果会与期望结果不一致
    其他解码器:
        ReplayingDecoder： 继承 ByteToMessageDecoder 参数 S指定用户状态管理的状态 其中Void表示不用状态管理
        使用这个类，就不需要调用readableBytes方法
        缺点:
            并不是所有的byteBuf操作都支持，不支持会抛出一个UnSupportedOperationException
            ReplayingDecoder 在某些情况下可能稍慢于ByteMessageDecoder，例如网络缓慢时且数据格式复杂，会分成碎片 速度变慢

        LineBasedFrameDecoder： 使用尾控制字符 \n \r\n作为分隔符来解析
        DelimiterBasedFrameDecoder：使用自定义的特殊字符作为消息的分隔符。
        HttpObjectDecoder:一个http数据的解码器
        LengthFieldBasedFrameDecoder:通过指定长度来标志整包消息，这样就可以自动处理粘包和半包消息

TCP粘包跟拆包:
    因为tcp是面向连接的，面向流的，提供高可靠服务，收发两端都是有一一对应的socket，因此发送端为了将多个发个接收端的包，更有效的发给对方，
    使用了优化算法(Nagle)算法，将多个间隔小的包合并成一个大的数据块，将其封包，虽然这么做提高了效率，但是接收端无法分辨出完整的数据包，
    因为面向流的通讯是无消息保护边界的。因此就有粘包拆包
    解决:
        使用自定义协议 + 编解码器
        解决服务器端每次读取数据长度的问题

ChannelPipeline调度handler:
    1.Context包装handler，多个Context在pipeline中形成双向链表，入站方向为inbound，在head节点开始，出站方法叫outbound，由tail节点开始；
    2.而节点中间的传递通过AbstractChannelHandlerContext类内部的fire系列方法，找到当前节点的下一个节点不断循环传播，是一个过滤器形式完成对handler的调度。

任务加入异步线程池:
    1.在handler中添加异步，可能更加自由，需要异步的地方异步，不用的就不需要，
    因为异步会拖长接口响应时间。
    因为需要将任务放进mpscTask中，如果IO时间很短，task很多，可能一个循环下来，都没有时间执行整个task，导致响应时间不达标；
    2.使用netty的标准方法(即加入到队列)，但是这样会将整个handler都交给业务线程池，不论耗时不耗时，都加入到队列中，不够灵活；


netty实现RPC:
    远程过程调用:一个计算机的程序允许调用另外一台的子程序，无须额外的交互作用编程
    Client为服务消费者，Server为服务提供者

    整体流程 :
    Client：           ClientStub                                       ServerStub:        Server：
    1.请求 -> 2.编码 -> 3.发送                                            4.接收 -> 5.解码 -> 6.调用api
    12.响应<-11.解码 <- 10.接收                                           9.发送 <- 8.编码 <- 7.响应