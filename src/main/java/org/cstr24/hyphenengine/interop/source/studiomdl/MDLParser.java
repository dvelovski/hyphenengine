package org.cstr24.hyphenengine.interop.source.studiomdl;

import org.cstr24.hyphenengine.filesystem.HyFile;
import org.cstr24.hyphenengine.geometry.Bone;
import org.cstr24.hyphenengine.interop.source.SourceInterop;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.*;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MDLParser {
    public static final Logger LOGGER = Logger.getLogger(MDLParser.class.getName());

    public MDLParser() {
    }

    public StudioModel parse(HyFile in){
        return parse(in, null);
    }
    public StudioModel parse(HyFile in, StudioModel parentFile) {
        try {
            //System.out.println("*** BEGIN PARSE OF " + in.getFileName() + " *** ");
            var fileBuffer = in.mapFile().order(ByteOrder.LITTLE_ENDIAN);

            studiohdr_t header;
            studiohdr2_t header2 = null;
            StudioMDLAnimationSectionParser animProcessor = new StudioMDLAnimationSectionParser();

            header = new studiohdr_t().parseStruct(fileBuffer);

            var mdlFile = new StudioModel(header, null);
            mdlFile.fileRef = in;
            mdlFile.reader = fileBuffer;
            mdlFile.parentFile = parentFile;

            boolean importParentBones = false;

            if (header.studiohdr2index != 0) {
                header2 = new studiohdr2_t().parseStruct(fileBuffer, header.studiohdr2index);

                String header2Name = SourceInterop.fetchNullTerminatedString(fileBuffer, header2.sznameindex, 64);
                //System.out.println("header2name: " + header2Name);

                /*var linearBone = new mstudiolinearbone_t().parseStruct(fileBuffer, header.studiohdr2index + header2.linearbone_index);
                System.out.println("*** linear bones ***");
                mdlFile.lBones = new ArrayList<>();
                mdlFile.linearBone = linearBone;
                for (int i = 0; i < header.numbones; i++){
                    var lBone = new LinearBone();
                    lBone.flags = linearBone.flags(fileBuffer, i);
                    lBone.parent = linearBone.parent(fileBuffer, i);
                    lBone.pos = linearBone.pos(fileBuffer, i);
                    lBone.posScale = linearBone.posScale(fileBuffer, i);
                    lBone.rot = linearBone.rot(fileBuffer, i);
                    lBone.rotScale = linearBone.rotScale(fileBuffer, i);
                    lBone.quat = linearBone.quat(fileBuffer, i);
                    lBone.qAlignment = linearBone.qAlignment(fileBuffer, i);

                    System.out.println("\tlinear bone " + i + ": flags " + lBone.flags + " & parent " + lBone.parent);

                    Bone hyBone = new Bone();
                    hyBone.basePosition = lBone.pos.toVec3f();
                    hyBone.baseRotation = lBone.quat.toQuaternionf();

                    if (lBone.parent != -1){
                        hyBone.parent = mdlFile.hyBones.get(lBone.parent);
                    }
                    hyBone.calculateInverseBindPose();

                    mdlFile.hyBones.add(hyBone);
                    mdlFile.lBones.add(lBone);
                }*/

                mdlFile.header2 = header2;
            }


            fileBuffer.position(header.boneindex);
            for (int bInd = 0; bInd < header.numbones; bInd++) {
                var bStruct = new mstudiobone_t().parseStruct(fileBuffer);
                mdlFile.bones.add(bStruct);

                Bone hyBone = new Bone();
                hyBone.basePosition = bStruct.pos;
                hyBone.baseRotation = bStruct.quat;
                hyBone.index = bInd;
                hyBone.name = bStruct.boneName;

                if (bStruct.parent != -1){
                    hyBone.parent = mdlFile.hyBones.get(bStruct.parent);
                }
                //hyBone.calculateInverseBindPose();

                mdlFile.hyBones.add(hyBone);
            }


            fileBuffer.position(header.localattachmentindex);
            for (int aInd = 0; aInd < header.numlocalattachments; aInd++) {
                mdlFile.attachments.add(new mstudioattachment_t().parseStruct(fileBuffer));
            }

            fileBuffer.position(header.textureindex);
            for (int txInd = 0; txInd < header.numtextures; txInd++) {
                mdlFile.mStudioTextureTs.add(new mstudiotexture_t().parseStruct(fileBuffer));
            }

            fileBuffer.position(header.bodypartindex);
            for (int bpInd = 0; bpInd < header.numbodyparts; bpInd++) {
                var bodyPartGroup = new mstudiobodyparts_t().parseStruct(fileBuffer);

                //let's get a studiomodel pointer
                //System.out.println("Body part group - num models: " + bodyPartGroup.nummodels);
                int preModelPos = fileBuffer.position();
                fileBuffer.position(bodyPartGroup.structPos + bodyPartGroup.modelindex);
                for (int mdInd = 0; mdInd < bodyPartGroup.nummodels; mdInd++) {
                    bodyPartGroup.models.add(new mstudiomodel_t().parseStruct(fileBuffer));
                }
                fileBuffer.position(preModelPos);

                mdlFile.bodyParts.add(bodyPartGroup);
            }

            fileBuffer.position(header.includemodel_index);
            for (int imInd = 0; imInd < header.includemodel_count; imInd++) {
                mdlFile.externalModelFiles.add(new mstudiomodelgroup_t().parseStruct(fileBuffer));
            }

            fileBuffer.position(header.localposeparamindex);
            for (int poInd = 0; poInd < header.numlocalposeparameters; poInd++) {
                var struct = new mstudioposeparamdesc_t().parseStruct(fileBuffer);
                mdlFile.poseParameters.add(struct);
            }

            fileBuffer.position(header.localanimindex);
            for (int anInd = 0; anInd < header.numlocalanim; anInd++) {
                var struct = new mstudioanimdesc_t().parseStruct(fileBuffer);
                mdlFile.animations.add(struct);

                int sectionCount = (struct.sectionframes != 0 ? struct.numframes / struct.sectionframes + 1 : 1);
                for (int i = 0; i < sectionCount; i++){
                    var sect = new mstudioanimdesc_t.Section();
                    sect.animDescs = animProcessor.parseAnimSection(mdlFile, struct, i);

                    struct.sections.add(sect);
                }
            }

            fileBuffer.position(header.localseqindex);

            for (int sqInd = 0; sqInd < header.numlocalseq; sqInd++) {
                var sequenceStruct = new mstudioseqdesc_t().parseStruct(fileBuffer);
                mdlFile.sequences.add(sequenceStruct);
                sequenceStruct.file = mdlFile;
                sequenceStruct.id = sqInd;
            }

            int skinTableSize = header.numskinref * header.numskinfamilies;
            mdlFile.skinRefs = new short[skinTableSize];

            fileBuffer.position(header.skinindex);
            for (int skInd = 0; skInd < skinTableSize; skInd++) {
                mdlFile.skinRefs[skInd] = fileBuffer.getShort();
            }

            fileBuffer.position(header.hitboxsetindex);
            for (int hsInd = 0; hsInd < header.numhitboxsets; hsInd++) {
                var hitboxSet = new mstudiohitboxset_t().parseStruct(fileBuffer);
                //I'm now interested in parsing the hitboxes
                int preHitBoxPos = fileBuffer.position();
                fileBuffer.position(hitboxSet.structPos + hitboxSet.hitboxindex);
                for (int hbInd = 0; hbInd < hitboxSet.numhitboxes; hbInd++) {
                    hitboxSet.hitboxes.add(new mstudiobbox_t().parseStruct(fileBuffer));
                }
                fileBuffer.position(preHitBoxPos);
            }

            fileBuffer.position(header.cdtextureindex);
            for (int tInd = 0; tInd < header.numcdtextures; tInd++) {
                int textureDirOffset = fileBuffer.getInt();

                int preStringOffset = fileBuffer.position();
                fileBuffer.position(textureDirOffset);
                String textureDir = SourceInterop.readNullTerminatedString(fileBuffer, 64, false);

                mdlFile.modelTextureDirectories.add(textureDir);
                fileBuffer.position(preStringOffset);
            }

            for (mstudiomodelgroup_t includeModel : mdlFile.externalModelFiles) {
                if (!includeModel.filename.isBlank()){
                    var includedModel = parse(HyFile.get(includeModel.filename), mdlFile);
                    mdlFile.importFile(includedModel);
                }
            }

            return mdlFile;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not load supplied MDL file.", e);
        }
        return null;
    }
}