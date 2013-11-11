package tiberiumcraft.client.objloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.client.model.ModelFormatException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 *  Wavefront Object importer
 *  Based heavily off of the specifications found at http://en.wikipedia.org/wiki/Wavefront_.obj_file
 *  
 *  Originally from MinecraftForge; modified for TiberiumCraft
 */
@SideOnly(Side.CLIENT)
public class AdvWavefrontObject implements IModelCustom
{

    private static Pattern Vector3fPattern = Pattern.compile("(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
    private static Pattern Vector3fNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
    private static Pattern Vector2fPattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *$)");
    private static Pattern face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
    private static Pattern face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
    private static Pattern face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
    private static Pattern face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
    private static Pattern groupObjectPattern = Pattern.compile("([go]( [\\w\\d]+) *\\n)|([go]( [\\w\\d]+) *$)");

    private static Matcher Vector3fMatcher, Vector3fNormalMatcher, Vector2fMatcher;
    private static Matcher face_V_VT_VN_Matcher, face_V_VT_Matcher, face_V_VN_Matcher, face_V_Matcher;
    private static Matcher groupObjectMatcher;

    public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
    public ArrayList<Vector3f> VertexNormals = new ArrayList<Vector3f>();
    public ArrayList<Vector3f> TextureCoordinates = new ArrayList<Vector3f>();
    public ArrayList<GroupObject> groupObjects = new ArrayList<GroupObject>();
    private GroupObject currentGroupObject;
    private String fileName;

    public AdvWavefrontObject(String fileName, URL resource) throws ModelFormatException
    {
        this.fileName = fileName;
        
        try
        {
            loadObjModel(resource.openStream());
        }
        catch (IOException e)
        {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
    }
    
    public AdvWavefrontObject(String filename, InputStream inputStream) throws ModelFormatException
    {
        this.fileName = filename;
        loadObjModel(inputStream);
    }

    private void loadObjModel(InputStream inputStream) throws ModelFormatException
    {
        BufferedReader reader = null;

        String currentLine = null;
        int lineCount = 0;

        try
        {
            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((currentLine = reader.readLine()) != null)
            {
                lineCount++;
                currentLine = currentLine.replaceAll("\\s+", " ").trim();

                if (currentLine.startsWith("#") || currentLine.length() == 0)
                {
                    continue;
                }
                else if (currentLine.startsWith("v "))
                {
                    Vector3f Vector3f = parseVector3f(currentLine, lineCount);
                    if (Vector3f != null)
                    {
                        vertices.add(Vector3f);
                    }
                }
                else if (currentLine.startsWith("vn "))
                {
                    Vector3f Vector3f = parseVector3fNormal(currentLine, lineCount);
                    if (Vector3f != null)
                    {
                        VertexNormals.add(Vector3f);
                    }
                }
                else if (currentLine.startsWith("vt "))
                {
                    Vector3f Vector2f = parseVector2f(currentLine, lineCount);
                    if (Vector2f != null)
                    {
                        TextureCoordinates.add(Vector2f);
                    }
                }
                else if (currentLine.startsWith("f "))
                {

                    if (currentGroupObject == null)
                    {
                        currentGroupObject = new GroupObject("Default");
                    }

                    Face face = parseFace(currentLine, lineCount);

                    if (face != null)
                    {
                        currentGroupObject.faces.add(face);
                    }
                }
                else if (currentLine.startsWith("g ") | currentLine.startsWith("o "))
                {
                    GroupObject group = parseGroupObject(currentLine, lineCount);

                    if (group != null)
                    {
                        if (currentGroupObject != null)
                        {
                            groupObjects.add(currentGroupObject);
                        }
                    }

                    currentGroupObject = group;
                }
            }

            groupObjects.add(currentGroupObject);
        }
        catch (IOException e)
        {
            throw new ModelFormatException("IO Exception reading model format", e);
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                // hush
            }

            try
            {
                inputStream.close();
            }
            catch (IOException e)
            {
                // hush
            }
        }
    }

    @Override
    public void renderAll()
    {
        Tessellator tessellator = Tessellator.instance;

        if (currentGroupObject != null)
        {
            tessellator.startDrawing(currentGroupObject.glDrawingMode);
        }
        else
        {
            tessellator.startDrawing(GL11.GL_TRIANGLES);
        }
        tessellateAll(tessellator);

        tessellator.draw();
    }

    public void tessellateAll(Tessellator tessellator)
    {
        for (GroupObject groupObject : groupObjects)
        {
            groupObject.render(tessellator);
        }
    }

    @Override
    public void renderOnly(String... groupNames)
    {
        for (GroupObject groupObject : groupObjects)
        {
            for (String groupName : groupNames)
            {
                if (groupName.equalsIgnoreCase(groupObject.name))
                {
                    groupObject.render();
                }
            }
        }
    }

    public void tessellateOnly(Tessellator tessellator, String... groupNames) {
        for (GroupObject groupObject : groupObjects)
        {
            for (String groupName : groupNames)
            {
                if (groupName.equalsIgnoreCase(groupObject.name))
                {
                    groupObject.render(tessellator);
                }
            }
        }
    }

    @Override
    public void renderPart(String partName)
    {
        for (GroupObject groupObject : groupObjects)
        {
            if (partName.equalsIgnoreCase(groupObject.name))
            {
                groupObject.render();
            }
        }
    }

    public void tessellatePart(Tessellator tessellator, String partName) {
        for (GroupObject groupObject : groupObjects)
        {
            if (partName.equalsIgnoreCase(groupObject.name))
            {
                groupObject.render(tessellator);
            }
        }
    }

    public void renderAllExcept(String... excludedGroupNames)
    {
        for (GroupObject groupObject : groupObjects)
        {
            boolean skipPart=false;
            for (String excludedGroupName : excludedGroupNames)
            {
                if (excludedGroupName.equalsIgnoreCase(groupObject.name))
                {
                    skipPart=true;
                }
            }
            if(!skipPart)
            {
                groupObject.render();
            }
        }
    }

    public void tessellateAllExcept(Tessellator tessellator, String... excludedGroupNames)
    {
        boolean exclude;
        for (GroupObject groupObject : groupObjects)
        {
            exclude=false;
            for (String excludedGroupName : excludedGroupNames)
            {
                if (excludedGroupName.equalsIgnoreCase(groupObject.name))
                {
                    exclude=true;
                }
            }
            if(!exclude)
            {
                groupObject.render(tessellator);
            }
        }
    }

    private Vector3f parseVector3f(String line, int lineCount) throws ModelFormatException
    {
        Vector3f Vector3f = null;

        if (isValidVector3fLine(line))
        {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try
            {
                if (tokens.length == 2)
                {
                    return new Vector3f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), 0);
                }
                else if (tokens.length == 3)
                {
                    return new Vector3f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
                }
            }
            catch (NumberFormatException e)
            {
                throw new ModelFormatException(String.format("Number formatting error at line %d",lineCount), e);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return Vector3f;
    }

    private Vector3f parseVector3fNormal(String line, int lineCount) throws ModelFormatException
    {
        Vector3f Vector3fNormal = null;

        if (isValidVector3fNormalLine(line))
        {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try
            {
                if (tokens.length == 3)
                    return new Vector3f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            }
            catch (NumberFormatException e)
            {
                throw new ModelFormatException(String.format("Number formatting error at line %d",lineCount), e);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return Vector3fNormal;
    }

    private Vector3f parseVector2f(String line, int lineCount) throws ModelFormatException
    {
        Vector3f Vector2f = null;

        if (isValidVector2fLine(line))
        {
            line = line.substring(line.indexOf(" ") + 1);
            String[] tokens = line.split(" ");

            try
            {
                if (tokens.length == 2)
                    return new Vector3f(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]), 0);
                else if (tokens.length == 3)
                    return new Vector3f(Float.parseFloat(tokens[0]), 1 - Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]));
            }
            catch (NumberFormatException e)
            {
                throw new ModelFormatException(String.format("Number formatting error at line %d",lineCount), e);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return Vector2f;
    }

    private Face parseFace(String line, int lineCount) throws ModelFormatException
    {
        Face face = null;

        if (isValidFaceLine(line))
        {
            face = new Face();

            String trimmedLine = line.substring(line.indexOf(" ") + 1);
            String[] tokens = trimmedLine.split(" ");
            String[] subTokens = null;

            if (tokens.length == 3)
            {
                if (currentGroupObject.glDrawingMode == -1)
                {
                    currentGroupObject.glDrawingMode = GL11.GL_TRIANGLES;
                }
                else if (currentGroupObject.glDrawingMode != GL11.GL_TRIANGLES)
                {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Invalid number of points for face (expected 4, found " + tokens.length + ")");
                }
            }
            else if (tokens.length == 4)
            {
                if (currentGroupObject.glDrawingMode == -1)
                {
                    currentGroupObject.glDrawingMode = GL11.GL_QUADS;
                }
                else if (currentGroupObject.glDrawingMode != GL11.GL_QUADS)
                {
                    throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Invalid number of points for face (expected 3, found " + tokens.length + ")");
                }
            }

            // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3 ...
            if (isValidFace_V_VT_VN_Line(line))
            {
                face.vertices = new Vector3f[tokens.length];
                face.textureCoordinates = new Vector3f[tokens.length];
                face.vertexNormals = new Vector3f[tokens.length];

                for (int i = 0; i < tokens.length; ++i)
                {
                    subTokens = tokens[i].split("/");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = TextureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                    face.vertexNormals[i] = VertexNormals.get(Integer.parseInt(subTokens[2]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1/vt1 v2/vt2 v3/vt3 ...
            else if (isValidFace_V_VT_Line(line))
            {
                face.vertices = new Vector3f[tokens.length];
                face.textureCoordinates = new Vector3f[tokens.length];

                for (int i = 0; i < tokens.length; ++i)
                {
                    subTokens = tokens[i].split("/");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.textureCoordinates[i] = TextureCoordinates.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1//vn1 v2//vn2 v3//vn3 ...
            else if (isValidFace_V_VN_Line(line))
            {
                face.vertices = new Vector3f[tokens.length];
                face.vertexNormals = new Vector3f[tokens.length];

                for (int i = 0; i < tokens.length; ++i)
                {
                    subTokens = tokens[i].split("//");

                    face.vertices[i] = vertices.get(Integer.parseInt(subTokens[0]) - 1);
                    face.vertexNormals[i] = VertexNormals.get(Integer.parseInt(subTokens[1]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            // f v1 v2 v3 ...
            else if (isValidFace_V_Line(line))
            {
                face.vertices = new Vector3f[tokens.length];

                for (int i = 0; i < tokens.length; ++i)
                {
                    face.vertices[i] = vertices.get(Integer.parseInt(tokens[i]) - 1);
                }

                face.faceNormal = face.calculateFaceNormal();
            }
            else
            {
                throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return face;
    }

    private GroupObject parseGroupObject(String line, int lineCount) throws ModelFormatException
    {
        GroupObject group = null;

        if (isValidGroupObjectLine(line))
        {
            String trimmedLine = line.substring(line.indexOf(" ") + 1);

            if (trimmedLine.length() > 0)
            {
                group = new GroupObject(trimmedLine);
            }
        }
        else
        {
            throw new ModelFormatException("Error parsing entry ('" + line + "'" + ", line " + lineCount + ") in file '" + fileName + "' - Incorrect format");
        }

        return group;
    }

    /***
     * Verifies that the given line from the model file is a valid Vector3f
     * @param line the line being validated
     * @return true if the line is a valid Vector3f, false otherwise
     */
    private static boolean isValidVector3fLine(String line)
    {
        if (Vector3fMatcher != null)
        {
            Vector3fMatcher.reset();
        }

        Vector3fMatcher = Vector3fPattern.matcher(line);
        return Vector3fMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid Vector3f normal
     * @param line the line being validated
     * @return true if the line is a valid Vector3f normal, false otherwise
     */
    private static boolean isValidVector3fNormalLine(String line)
    {
        if (Vector3fNormalMatcher != null)
        {
            Vector3fNormalMatcher.reset();
        }

        Vector3fNormalMatcher = Vector3fNormalPattern.matcher(line);
        return Vector3fNormalMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid texture coordinate
     * @param line the line being validated
     * @return true if the line is a valid texture coordinate, false otherwise
     */
    private static boolean isValidVector2fLine(String line)
    {
        if (Vector2fMatcher != null)
        {
            Vector2fMatcher.reset();
        }

        Vector2fMatcher = Vector2fPattern.matcher(line);
        return Vector2fMatcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices, texture coordinates, and Vector3f normals
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1/vt1/vn1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VT_VN_Line(String line)
    {
        if (face_V_VT_VN_Matcher != null)
        {
            face_V_VT_VN_Matcher.reset();
        }

        face_V_VT_VN_Matcher = face_V_VT_VN_Pattern.matcher(line);
        return face_V_VT_VN_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices and texture coordinates
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1/vt1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VT_Line(String line)
    {
        if (face_V_VT_Matcher != null)
        {
            face_V_VT_Matcher.reset();
        }

        face_V_VT_Matcher = face_V_VT_Pattern.matcher(line);
        return face_V_VT_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by vertices and Vector3f normals
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1//vn1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_VN_Line(String line)
    {
        if (face_V_VN_Matcher != null)
        {
            face_V_VN_Matcher.reset();
        }

        face_V_VN_Matcher = face_V_VN_Pattern.matcher(line);
        return face_V_VN_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face that is described by only vertices
     * @param line the line being validated
     * @return true if the line is a valid face that matches the format "f v1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
     */
    private static boolean isValidFace_V_Line(String line)
    {
        if (face_V_Matcher != null)
        {
            face_V_Matcher.reset();
        }

        face_V_Matcher = face_V_Pattern.matcher(line);
        return face_V_Matcher.matches();
    }

    /***
     * Verifies that the given line from the model file is a valid face of any of the possible face formats
     * @param line the line being validated
     * @return true if the line is a valid face that matches any of the valid face formats, false otherwise
     */
    private static boolean isValidFaceLine(String line)
    {
        return isValidFace_V_VT_VN_Line(line) || isValidFace_V_VT_Line(line) || isValidFace_V_VN_Line(line) || isValidFace_V_Line(line);
    }

    /***
     * Verifies that the given line from the model file is a valid group (or object)
     * @param line the line being validated
     * @return true if the line is a valid group (or object), false otherwise
     */
    private static boolean isValidGroupObjectLine(String line)
    {
        if (groupObjectMatcher != null)
        {
            groupObjectMatcher.reset();
        }

        groupObjectMatcher = groupObjectPattern.matcher(line);
        return groupObjectMatcher.matches();
    }

    @Override
    public String getType()
    {
        return "obj";
    }
}
