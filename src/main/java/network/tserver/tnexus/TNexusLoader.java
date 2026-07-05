package network.tserver.tnexus;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;

public final class TNexusLoader implements PluginLoader {
	@Override
	public void classloader(PluginClasspathBuilder classpathBuilder) {
		MavenLibraryResolver resolver = new MavenLibraryResolver();

		resolver.addDependency(
			new Dependency(
				new DefaultArtifact("com.electronwill.night-config:toml:3.9.0"),
				null
			)
		);

		resolver.addRepository(
			new RemoteRepository.Builder(
				"central",
				"default",
				MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR
			).build()
		);

		classpathBuilder.addLibrary(resolver);
	}
}