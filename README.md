# jersey-handlebars

View processor implementation for [Jersey](http://jersey.java.net/) to render [Handlebars](http://handlebarsjs.com/) templates using [Handlebars.java](https://github.com/jknack/handlebars.java).

## Installation

### Maven

Ultimately, this repository should also be available from maven. Until we figure out how to publish it, you'll need to manually download and build this project :)


## Initialization

You need to add the following init-param to your web.xml to your Jersey servlet configuration:

```xml
<init-param>
	<param-name>jersey.config.server.provider.classnames</param-name>
	<param-value>com.seekda.jersey.feature.handlebars.HandlebarsMvcFeature</param-value>
</init-param>
```

## Usage

### Basic usage

If you have correctly installed and initialized this plugin, you are already good to go!

Imagine the following class:

```java
package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.glassfish.jersey.server.mvc.Template;

@Path("test")
public class Test {
	@GET
	@Template(name = "detail")
	public String showUser() {
		return "Arthur Dent";
	}
}
```

Now you just need a template located at `src/main/resources/com/example/Test/detail.handlebars`:

```
Do you have your towel with you, {{this}}?
```

If you start your server now and hit `/test`, you should see the following output:

```
Do you have your towel with you, Arthur Dent?
```

### Custom helpers

You also have the possibility to provide custom helpers on a per-page basis. To use them, you need to return a
`HandlebarsModel` in your method:

```java
package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.mvc.Template;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.seekda.jersey.feature.handlebars.HandlebarsModel;

@Path("test")
@Produces(MediaType.TEXT_HTML)
public class Test {
	@GET
	@Template(name = "helper")
	public HandlebarsModel<String> withHelper () {
		HandlebarsModel<String> mdl = new HandlebarsModel<>();

		mdl.getHandlebars().registerHelper("emphasize", new Helper<String>() {
			public CharSequence apply(String input, Options options) {
				return new Handlebars.SafeString("<strong>" + input + "</strong>");
			}
		});
		mdl.setData("Sir Arthur Conan Doyle");

		return mdl;
	}
}
```

Now, with your template at `src/main/resources/com/example/Test/helper.handlebars`:

```
{{emphasize this}} wrote Sherlock Holmes.
```

You should get the following HTML-Code:

```html
<strong>Sir Arthur Conan Doyle</strong> wrote Sherlock Holmes.
```

For more information about helpers, visit the page of [Handlebars.java](https://github.com/jknack/handlebars.java).

## Configuration options
There are other, additional options you can set to further customize the usage of Handlebars:

### Template base path
This is a path relative to the `classpath`, where your templates will be searched.
For example, if you have a folder `src/main/resources/templates/` with all your templates, you'd set this
to `/templates`:

```xml
<init-param>
	<param-name>jersey.config.server.mvc.templateBasePath.handlebars</param-name>
	<param-value>/templates</param-value>
</init-param>
```

### Handlers
You can configure one or more classes to provide static helpers for all of your handlebars files.
Imagine you have a class `com.example.DefaultHelpers` with a static method `now()` to print the current time,
you'd set this option and then you can use the `{{now}}` helper in all of your files:

```xml
<init-param>
	<param-name>jersey.config.server.mvc.helpers.handlebars</param-name>
	<param-value>com.example.DefaultHelpers</param-value>
</init-param>
```

### Custom file suffix
If you template files have a different suffix than ".handlebars", e.g. ".hbs", you can set this with this option:

```xml
<init-param>
	<param-name>jersey.config.server.mvc.suffix.handlebars</param-name>
	<param-value>hbs</param-value>
</init-param>
```

Note that there must not be a dot in the beginning of the value.