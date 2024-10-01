package net.hyze.core.shared.updater;

import com.google.common.base.Objects;
import net.hyze.core.shared.environment.Env;
import java.nio.file.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import org.apache.commons.codec.digest.DigestUtils;

public class JarUpdater {

    private static final File JARS_DIRECTORY = new File(Env.getString("paths.jars", "/home/minecraft/projects/outputs"));

    private final File local, cloud;

    public JarUpdater(File local) {
        this.local = local;
        this.cloud = new File(JARS_DIRECTORY, local.getName());
    }

    public void update() {
        try {
            Files.copy(this.cloud.toPath(), this.local.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public JarState getState() {
        String localMd5;

        if (!this.local.exists()) {
            return JarState.OTHER;
        }

        try {
            localMd5 = DigestUtils.md5Hex(new FileInputStream(this.local));
        } catch (IOException e) {
            return JarState.OTHER;
        }

        String cloudMd5;

        try {
            cloudMd5 = DigestUtils.md5Hex(new FileInputStream(this.cloud));
        } catch (IOException e) {
            return JarState.OTHER;
        }

        if (!Objects.equal(localMd5, cloudMd5)) {
            return JarState.OUT_OF_DATE;
        } else {
            return JarState.UPDATED;

        }
    }

}
