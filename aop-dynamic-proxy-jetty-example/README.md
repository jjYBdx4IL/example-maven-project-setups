# aop-dynamic-proxy-jetty-example

A maven example demonstrating how to use AOP and Java dynamic proxies to weave servlet layer service calls. 

Beware! Java dynamic proxies are very explicit in their nature: they only work if code calls the proxy object, so
any call made from within a class to one of its member functions bypasses the proxy object. If you want, for example,
implement transaction handling around GWT service layer calls, you'd have to override the parts of RemoteServiceSerlvet
that do the final call of your service layer methods
([link](https://doanduyhai.wordpress.com/2012/07/29/gwt-rpc-integration-with-spring/)). This, of course, extends to
all similar applications.