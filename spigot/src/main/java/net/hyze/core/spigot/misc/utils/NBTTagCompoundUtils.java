package net.hyze.core.spigot.misc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class NBTTagCompoundUtils {

    public static String serialize(NBTTagCompound compound) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            NBTCompressedStreamTools.a(compound, outputStream);
        } catch (IOException ex) {
            ex.printStackTrace();

            return null;
        }

        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    public static NBTTagCompound deserialize(String compound) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(compound));

        try {
            return NBTCompressedStreamTools.a(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static NBTTagList toList(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            nbttaglist.add(new NBTTagDouble(d0));
        }

        return nbttaglist;
    }
}
