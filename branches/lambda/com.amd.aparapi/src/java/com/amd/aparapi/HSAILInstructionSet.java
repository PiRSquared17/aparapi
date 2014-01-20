package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;

/**
 * Created by user1 on 1/14/14.
 */
public class HSAILInstructionSet {
        abstract static class HSAILInstruction<H extends HSAILInstruction<H>>  {
            String location;
            Instruction from;
            HSAILRegister[] dests = null;
            HSAILOperand[] sources = null;
           // HSAILStackFrame hsailStackFrame = null;

            HSAILInstruction(HSAILInstruction original) {
                from = original.from;
               // hsailStackFrame = original.hsailStackFrame;
                if (original.dests == null){
                    dests = null;
                }else{
                    dests = new HSAILRegister[original.dests.length];
                    for (int i=0; i<dests.length; i++){
                        dests[i] = original.dests[i].cloneMe();
                    }
                }
                if (original.sources == null){
                    sources = null;
                }else{
                    sources = new HSAILRegister[original.sources.length];
                    for (int i=0; i<sources.length; i++){
                        sources[i] = original.sources[i].cloneMe();
                    }
                }

            }

            HSAILInstruction(HSAILStackFrame _hsailStackFrame,Instruction _from, int _destCount, int _sourceCount) {
              //  hsailStackFrame = _hsailStackFrame;
                from = _from;
                dests = new HSAILRegister[_destCount];
                sources = new HSAILOperand[_sourceCount];
                location = _hsailStackFrame.getUniqueLocation(from.getStartPC());
            }

            public abstract  H cloneMe();


           // public HSAILStackFrame getHSAILStackFrame(){
             //   return(hsailStackFrame);
           // }
            abstract void render(HSAILRenderer r);

        }

        abstract static class HSAILInstructionWithDest<H extends HSAILInstructionWithDest<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

            protected HSAILInstructionWithDest(  H original){
                super(original);

            }

            HSAILInstructionWithDest(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, 1, 0);
                dests[0] = _dest;
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }
        }

        abstract static class HSAILInstructionWithSrc<H extends HSAILInstructionWithSrc<H,Rt,T>, Rt extends HSAILOperand<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

            protected HSAILInstructionWithSrc( H original){
                super(original);
            }


            HSAILInstructionWithSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src) {
                super(_hsailStackFrame,_from, 0, 1);
                sources[0] = _src;
            }

            Rt getSrc() {
                return ((Rt) sources[0]);
            }
        }

        abstract static class HSAILInstructionWithSrcSrc<H extends HSAILInstructionWithSrcSrc<H,Rt,T>, Rt extends HSAILOperand<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

            protected HSAILInstructionWithSrcSrc(H original){
                super(original);
            }
            HSAILInstructionWithSrcSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src_lhs, Rt _src_rhs) {
                super(_hsailStackFrame,_from, 0, 2);
                sources[0] = _src_lhs;
                sources[1] = _src_rhs;
            }

            Rt getSrcLhs() {
                return ((Rt) sources[0]);
            }

            Rt getSrcRhs() {
                return ((Rt) sources[1]);
            }
        }

        abstract static class HSAILInstructionWithDestSrcSrc<H extends HSAILInstructionWithDestSrcSrc<H,Rd,Rlhs,Rrhs,D,Tlhs, Trhs>, Rd extends HSAILRegister<Rd,D>, Rlhs extends HSAILOperand<Rlhs,Tlhs>, Rrhs extends HSAILOperand<Rrhs,Trhs>,D extends PrimitiveType, Tlhs extends PrimitiveType, Trhs extends PrimitiveType> extends HSAILInstruction<H> {

            protected HSAILInstructionWithDestSrcSrc(H original){
                super(original);
            }
            HSAILInstructionWithDestSrcSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rlhs _src_lhs, Rrhs _src_rhs) {
                super(_hsailStackFrame,_from, 1, 2);
                dests[0] = _dest;
                sources[0] = _src_lhs;
                sources[1] = _src_rhs;
            }

            Rd getDest() {
                return ((Rd) dests[0]);
            }

            Rlhs getSrcLhs() {
                return ((Rlhs) sources[0]);
            }

            Rrhs getSrcRhs() {
                return ((Rrhs) sources[1]);
            }
        }



        abstract static class HSAILInstructionWithDestSrc<H extends HSAILInstructionWithDestSrc<H,Rd,Rt,D,T>, Rd extends HSAILRegister<Rd,D>, Rt extends HSAILOperand<Rt,T>, D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction<H> {
            HSAILInstructionWithDestSrc(H original){
                super(original);
            }
            HSAILInstructionWithDestSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src) {
                super(_hsailStackFrame,_from, 1, 1);
                dests[0] = _dest;
                sources[0] = _src;
            }

            Rd getDest() {
                return ((Rd) dests[0]);
            }

            Rt  getSrc() {
                return ((Rt) sources[0]);
            }
        }

        static  class branch <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<branch<R>,R, s32> {
            String branchName;
            int pc;
            String targetLabel;

            protected branch(branch<R> original){
                super(original);
                branchName = original.branchName;
                pc = original.pc;
               targetLabel = original.targetLabel;
            }

            branch(HSAILStackFrame _hsailStackFrame,Instruction _from, R _src, String _branchName, int _pc) {
                super(_hsailStackFrame,_from, _src);
                branchName = _branchName;
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }

            @Override public branch<R> cloneMe(){
                return(new branch<R>(this));
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append(branchName).space().label(targetLabel).semicolon();
            }
        }

        static  class cmp_s32_const_0 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<cmp_s32_const_0<R>,R, s32> {
            String type;

            protected cmp_s32_const_0(cmp_s32_const_0<R> original){
                super(original);
                type = original.type;
            }

            cmp_s32_const_0(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _src) {
                super(_hsailStackFrame, _from, _src);
                type = _type;
            }

            @Override public cmp_s32_const_0<R> cloneMe(){
                return(new cmp_s32_const_0<R>(this));
            }

            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrc()).space().append("$c1").separator().operandName(getSrc()).separator().append("0").semicolon();

            }
        }

        static  class cmp_s32 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrcSrc<cmp_s32<R>,R, s32> {

            String type;

            protected cmp_s32(cmp_s32<R> original){
                super(original);
                type = original.type;
            }

            cmp_s32(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
                super(_hsailStackFrame,_from, _srcLhs, _srcRhs);
                type = _type;
            }

            @Override public cmp_s32<R> cloneMe(){
                return(new cmp_s32<R>(this));
            }

            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }
        static  class cmp_ref <R extends HSAILRegister<R,ref>> extends HSAILInstructionWithSrcSrc<cmp_ref<R>,R, ref> {

            String type;

            protected cmp_ref(cmp_ref<R> original){
                super(original);
                type = original.type;
            }

            cmp_ref(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
                super(_hsailStackFrame, _from, _srcLhs, _srcRhs);
                type = _type;
            }

            @Override public cmp_ref<R> cloneMe(){
                return(new cmp_ref<R>(this));
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }

        static   class cmp<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrcSrc<cmp<Rt,T>,Rt, T> {

            String type;

            protected cmp(cmp<Rt,T> original){
                super(original);
                type = original.type;
            }

            cmp(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, Rt _srcLhs, Rt _srcRhs) {
                super(_hsailStackFrame,_from, _srcLhs, _srcRhs);
                type = _type;
            }

            @Override public cmp<Rt,T> cloneMe(){
                return(new cmp<Rt,T>(this));
            }

            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("u").append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }

        static  class cbr extends HSAILInstruction<cbr> {

            int pc;
            String targetLabel;

            protected cbr(cbr original){
                super(original);
                pc = original.pc;
               targetLabel = original.targetLabel;
            }

            cbr(HSAILStackFrame _hsailStackFrame,Instruction _from, int _pc) {
                super(_hsailStackFrame,_from, 0, 0);
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }

            @Override public cbr cloneMe(){
                return(new cbr(this));
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cbr").space().append("$c1").separator().label(targetLabel).semicolon();

            }
        }

        static  class brn extends HSAILInstruction<brn> {
            int pc;
            String targetLabel;
            protected brn(brn original){
                super(original);
                pc = original.pc;
               targetLabel = original.targetLabel;
            }



            brn(HSAILStackFrame _hsailStackFrame,Instruction _from, int _pc) {
                super(_hsailStackFrame, _from, 0, 0);
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }

            @Override public brn cloneMe(){
                return(new brn(this));
            }

            @Override
            public void render(HSAILRenderer r) {
                r.append("brn").space().label(targetLabel).semicolon();

            }
        }
   static  class inlineReturnBrn extends HSAILInstruction<inlineReturnBrn> {

      String targetLabel;
      protected inlineReturnBrn(inlineReturnBrn original){
         super(original);
         targetLabel = original.targetLabel;
      }

      inlineReturnBrn(HSAILStackFrame _hsailStackFrame,Instruction _from, String _targetLabel) {
         super(_hsailStackFrame, _from, 0, 0);
         targetLabel = _targetLabel;
      }

      @Override public inlineReturnBrn cloneMe(){
         return(new inlineReturnBrn(this));
      }

      @Override
      public void render(HSAILRenderer r) {
         r.append("brn").space().label(targetLabel).semicolon().lineComment("mapped from return");

      }
   }






        static  class nyi extends HSAILInstruction<nyi> {

            protected nyi(nyi original){
                super(original);
            }

            nyi(HSAILStackFrame _hsailStackFrame,Instruction _from) {
                super(_hsailStackFrame, _from, 0, 0);
            }

            @Override public nyi cloneMe(){
                return(new nyi(this));
            }

            @Override
            void render(HSAILRenderer r) {

                r.append("NYI ").i(from);

            }
        }

        static  class ld_kernarg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_kernarg<Rt,T>,Rt, T> {

            protected ld_kernarg(ld_kernarg<Rt,T> original){
                super(original);

            }

            ld_kernarg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, _dest);
            }

            @Override public ld_kernarg<Rt,T> cloneMe(){
                return(new ld_kernarg<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_kernarg_").typeName(getDest()).space().operandName(getDest()).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
            }
        }

    static  class workitemabsid<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<workitemabsid<Rt,T>,Rt, T> {

        protected workitemabsid(workitemabsid<Rt,T> original){
            super(original);

        }

        workitemabsid(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }

        @Override public workitemabsid<Rt,T> cloneMe(){
            return(new workitemabsid<Rt,T>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("workitemabsid_").typeName(getDest()).space().operandName(getDest()).separator().append("0").semicolon();
        }
    }

        static  class ld_arg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_arg<Rt,T>,Rt, T> {

            protected ld_arg(ld_arg<Rt,T> original){
                super(original);

            }

            ld_arg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, _dest);
            }

            @Override public ld_arg cloneMe(){
                return(new ld_arg(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_arg_").typeName(getDest()).space().operandName(getDest()).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
            }


        }

        static  abstract class binary_const<H extends binary_const<H, Rt, T, C>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDestSrc<H, Rt,Rt,T,T> {
            C value;
            String op;

            protected binary_const(H original){
                super(original);
                value = original.value;
                op = original.op;
            }

            binary_const(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, _dest, _src);
                value = _value;
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getSrc()).separator().append(value).semicolon();
            }


        }

        static  class add_const<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends binary_const<add_const<Rt, T, C>, Rt,T, C> {
            protected add_const(add_const<Rt,T,C> original){
                super(original);
            }

            add_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, "add_", _dest, _src, _value);

            }
            @Override public add_const<Rt,T,C> cloneMe(){
                return(new add_const<Rt,T,C>(this));
            }

        }

        static   class and_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const<and_const<Rt, T,C>, Rt, T, C> {

            protected and_const(and_const<Rt, T,C> original){
                super(original);
            }

            and_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest,Rt _src, C _value) {
                super(_hsailStackFrame,_from, "and_", _dest, _src, _value);

            }

            @Override public and_const<Rt, T,C> cloneMe(){
                return(new and_const<Rt, T,C>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).append("b64").space().operandName(getDest()).separator().operandName(getSrc()).separator().append(value).semicolon();
            }


        }

        static  class mul_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const< mul_const<Rt, T,C>, Rt, T, C> {
            protected mul_const(mul_const<Rt,T,C> original){
                super(original);
            }

            mul_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, "mul_", _dest, _src, _value);

            }

            @Override public mul_const<Rt,T,C> cloneMe(){
                return(new mul_const<Rt,T,C>(this));
            }

        }

        static class mad<Rd extends HSAILRegister<Rd,ref>, Rt extends HSAILRegister<Rt,ref>> extends HSAILInstructionWithDestSrcSrc<mad<Rd,Rt>, Rd, Rt,Rt, ref, ref, ref> {
            long size;
            protected mad(mad<Rd,Rt> original){
                super(original);
                size = original.size;
            }

            mad(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src_lhs, Rt _src_rhs, long _size) {
                super(_hsailStackFrame, _from, _dest, _src_lhs, _src_rhs);
                size = _size;
            }

            @Override public mad<Rd,Rt> cloneMe(){
                return(new mad<Rd,Rt>(this));
            }

            @Override void render(HSAILRenderer r) {
                r.append("mad_").typeName(getDest()).space().operandName(getDest()).separator().operandName(getSrcLhs()).separator().append(size).separator().operandName(getSrcRhs()).semicolon();
            }
        }


        static   class cvt<Rt1 extends HSAILRegister<Rt1,T1>, Rt2 extends HSAILRegister<Rt2,T2>,T1 extends PrimitiveType, T2 extends PrimitiveType> extends HSAILInstruction<cvt<Rt1,Rt2,T1,T2>> {

            protected cvt(cvt<Rt1,Rt2,T1,T2> original){
                super(original);


            }
            @Override public cvt<Rt1,Rt2,T1,T2> cloneMe(){
                return(new cvt(this));
            }
            cvt(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt1 _dest, Rt2 _src) {
                super(_hsailStackFrame,_from, 1, 1);
                dests[0] = _dest;
                sources[0] = _src;
            }

            Rt1 getDest() {
                return ((Rt1) dests[0]);
            }

            Rt2 getSrc() {
                return ((Rt2) sources[0]);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().operandName(getDest()).separator().operandName(getSrc()).semicolon();
            }


        }


        static  class retvoid extends HSAILInstruction<retvoid> {
            protected retvoid(retvoid original){
                super(original);


            }
            @Override public retvoid cloneMe(){
                return(new retvoid(this));
            }

            retvoid(HSAILStackFrame _hsailStackFrame,Instruction _from) {
                super(_hsailStackFrame,_from, 0, 0);

            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ret").semicolon();
            }


        }

        static  class ret<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<ret<Rt,T>,Rt, T> {

            String endLabel;
            protected ret(ret<Rt,T> original){
                super(original);


            }
            @Override public ret<Rt,T> cloneMe(){
                return(new ret<Rt,T>(this));
            }
            ret(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src) {
                super(_hsailStackFrame,_from, _src);
                endLabel = _hsailStackFrame.getUniqueName()+"_END";

            }

            @Override
            void render(HSAILRenderer r) {
                r.append("st_arg_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[%_result]").semicolon().nl();
                r.append("ret").semicolon();
            }


        }

        static  class array_store<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<array_store<Rt, T>,Rt, T> {
            Reg_ref mem;

            protected array_store(array_store<Rt, T> original){
                super(original);
                mem = original.mem;
            }

            array_store(HSAILStackFrame _hsailStackFrame,Instruction _from, Reg_ref _mem, Rt _src) {
                super(_hsailStackFrame,_from, _src);
                mem = _mem;
            }

            @Override public array_store<Rt, T> cloneMe(){
                return(new array_store<Rt, T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                // r.append("st_global_").typeName(getSrc()).space().append("[").operandName(mem).append("+").array_len_offset().append("]").separator().operandName(getSrc());
                r.append("st_global_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[").operandName(mem).append("+").array_base_offset().append("]").semicolon();
            }


        }


        static   class array_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<array_load<Rt,T>,Rt,T> {
            Reg_ref mem;

            protected array_load(array_load<Rt,T> original){
                super(original);
                mem = original.mem;
            }

            array_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Reg_ref _mem) {
                super(_hsailStackFrame,_from, _dest);
                mem = _mem;
            }

            @Override public array_load<Rt,T> cloneMe(){
                return(new array_load<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").array_base_offset().append("]").semicolon();
                if (getDest().type.getHsaBits()==8){
                    r.nl().pad(9).append("//cvt_s32_u8 $s").regNum(getDest()).separator().space().operandName(getDest()).semicolon();
                }     else   if (getDest().type.getHsaBits()==16){
                    r.nl().pad(9).append("//cvt_s32_u16 $s").regNum(getDest()).separator().space().operandName(getDest()).semicolon();
                }
            }


        }

        static  class array_len<Rs32 extends HSAILRegister<Rs32,s32>> extends HSAILInstructionWithDest<array_len<Rs32>, Rs32, s32> {
            Reg_ref mem;

            protected array_len(array_len<Rs32> original){
                super(original);
                mem = original.mem;
            }

            array_len(HSAILStackFrame _hsailStackFrame,Instruction _from, Rs32 _dest, Reg_ref _mem) {
                super(_hsailStackFrame,_from, _dest);
                mem = _mem;
            }

            @Override public array_len<Rs32> cloneMe(){
                return(new array_len<Rs32>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").array_len_offset().append("]").semicolon();
            }


        }

        static  class field_load<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDest<field_load<Rt,T>, Rt,T> {

            Reg_ref mem;
            long offset;
            protected field_load(field_load<Rt,T> original){
                super(original);
                mem = original.mem;
                offset = original.offset;
            }

            field_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Reg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _dest);
                offset = _offset;
                mem = _mem;
            }

            @Override public field_load<Rt,T> cloneMe(){
                return(new field_load<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }

        static  class static_field_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<static_field_load<Rt,T>,Rt, T> {
            long offset;
            Reg_ref mem;
            protected static_field_load(static_field_load<Rt,T> original){
                super(original);
                mem = original.mem;
                offset = original.offset;
            }

            static_field_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Reg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _dest);
                offset = _offset;
                mem = _mem;
            }

            @Override public static_field_load<Rt,T> cloneMe(){
                return(new static_field_load<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }


        static  class field_store<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithSrc<field_store<Rt,T>,Rt,T> {

            Reg_ref mem;
            long offset;

            protected field_store(field_store<Rt,T> original){
                super(original);
                mem = original.mem;
                offset = original.offset;
            }

            field_store(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src, Reg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _src);
                offset = _offset;
                mem = _mem;
            }

            @Override public field_store<Rt,T> cloneMe(){
                return(new field_store<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("st_global_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }


        static final class mov<Rd extends HSAILRegister<Rd,D>,Rt extends HSAILRegister<Rt,T>,D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<mov<Rd,Rt,D,T>, Rd, Rt,D,T> {
            protected mov(mov<Rd,Rt,D,T> original){
                super(original);

            }

            public mov(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src) {
                super(_hsailStackFrame,_from, _dest, _src);
            }
            @Override public mov<Rd,Rt,D,T> cloneMe(){
                return(new mov<Rd,Rt,D,T>(this));
            }
            @Override
            void render(HSAILRenderer r) {
                r.append("mov_").movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getSrc()).semicolon();

            }


        }

   static final class returnMov<Rd extends HSAILRegister<Rd,D>,Rt extends HSAILRegister<Rt,T>,D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<returnMov<Rd,Rt,D,T>, Rd, Rt,D,T> {
       String endLabel;

      protected returnMov(returnMov<Rd,Rt,D,T> original){
         super(original);
         endLabel = original.endLabel;
      }

      public returnMov(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src, String _endLabel) {
         super(_hsailStackFrame,_from, _dest, _src);
         endLabel = _endLabel;
      }
      @Override public returnMov<Rd,Rt,D,T> cloneMe(){
         return(new returnMov<Rd,Rt,D,T>(this));
      }
      @Override
      void render(HSAILRenderer r) {
         r.append("mov_").movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getSrc()).semicolon().nl().label(endLabel).colon();

      }


   }

        static  abstract class unary<H extends unary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<H,Rt,Rt, T,T> {
            String op;

            protected unary(H original){
                super(original);
                op = original.op;
            }

            public unary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _destSrc) {
                super(_hsailStackFrame,_from, _destSrc, _destSrc);
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getDest()).semicolon();
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }

            Rt getSrc() {
                return ((Rt) sources[0]);
            }


        }

        static  abstract class binary<H extends binary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {
            String op;
            protected binary(H original){
                super(original);
                op = original.op;

            }
            public binary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, 1, 2);
                dests[0] = _dest;
                sources[0] = _lhs;
                sources[1] = _rhs;
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }

            Rt getRhs() {
                return ((Rt) sources[1]);
            }

            Rt getLhs() {
                return ((Rt) sources[0]);
            }


        }

  /*  abstract class binaryRegConst<T extends JavaType, C> extends HSAILInstruction{
      HSAILRegister<T> dest, lhs;
      C value;
      String op;

      public binaryRegConst(Instruction _from, String _op,  HSAILRegister<T> _dest, HSAILRegister<T> _lhs, C _value){
         super(_from);
         dest = _dest;
         lhs = _lhs;
         value = _value;
         op = _op;
      }
      @Override void renderDefinition(HSAILRenderer r){
         r.append(op).typeName(dest).space().operandName(dest).separator().operandName(lhs).separator().append(value.toString());
      }
   }

     class addConst<T extends JavaType, C> extends binaryRegConst<T, C>{

      public addConst(Instruction _from,   HSAILRegister<T> _dest, HSAILRegister<T> _lhs, C _value_rhs){
         super(_from, "add_", _dest, _lhs, _value_rhs);
      }
   }
   */

        static   class add<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<add<Rt,T>, Rt, T> {
            protected add(add<Rt,T> original){
                super(original);
            }

            public add(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "add_", _dest, _lhs, _rhs);
            }
            @Override public add<Rt,T> cloneMe(){
                return (new add<Rt,T>(this));
            }

        }

        static   class sub<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<sub<Rt,T>, Rt, T> {
            protected sub(sub<Rt,T> original){
                super(original);
            }

            public sub(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "sub_", _dest, _lhs, _rhs);
            }
            @Override public sub<Rt,T> cloneMe(){
                return (new sub<Rt,T>(this));
            }
        }

        static  class div<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<div<Rt,T>, Rt, T> {

            @Override public div<Rt,T> cloneMe(){
                return (new div<Rt,T>(this));
            }
            public div(HSAILStackFrame _hsailStackFrame,Instruction _from,Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "div_", _dest, _lhs, _rhs);
            }
            protected div(div<Rt,T> original){
                super(original);
            }
        }

        static  class mul<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<mul<Rt,T>, Rt, T> {
            protected mul(mul<Rt,T> original){
                super(original);
            }
            @Override public mul<Rt,T> cloneMe(){
                return (new mul<Rt,T>(this));
            }
            public mul(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "mul_", _dest, _lhs, _rhs);
            }

        }

        static   class rem<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<rem<Rt,T>, Rt, T> {
            protected rem(rem<Rt,T> original){
                super(original);
            }
            @Override public rem<Rt,T> cloneMe(){
                return (new rem<Rt,T>(this));
            }
            public rem(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "rem_", _dest, _lhs, _rhs);
            }

        }

        static  class neg<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<neg<Rt,T>, Rt, T> {

            protected neg(neg<Rt,T> original){
                super(original);
            }
            @Override public neg<Rt,T> cloneMe(){
                return (new neg<Rt,T>(this));
            }
            public neg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
                super(_hsailStackFrame,_from, "neg_", _destSrc);
            }

        }

    static  class nsqrt<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsqrt<Rt,T>, Rt, T> {

        protected nsqrt(nsqrt<Rt,T> original){
            super(original);
        }
        @Override public nsqrt<Rt,T> cloneMe(){
            return (new nsqrt<Rt,T>(this));
        }
        public nsqrt(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "nsqrt_", _destSrc);
        }

    }

    static  class ncos<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<ncos<Rt,T>, Rt, T> {

        protected ncos(ncos<Rt,T> original){
            super(original);
        }
        @Override public ncos<Rt,T> cloneMe(){
            return (new ncos<Rt,T>(this));
        }
        public ncos(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "ncos_", _destSrc);
        }

    }

    static  class nsin<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsin<Rt,T>, Rt, T> {

        protected nsin(nsin<Rt,T> original){
            super(original);
        }
        @Override public nsin<Rt,T> cloneMe(){
            return (new nsin<Rt,T>(this));
        }
        public nsin(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "nsin_", _destSrc);
        }

    }



        static  class shl<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shl<Rt,T>, Rt, T> {
            protected shl(shl<Rt,T> original){
                super(original);
            }
            @Override public shl<Rt,T> cloneMe(){
                return (new shl<Rt,T>(this));
            }
            public shl(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "shl_", _dest, _lhs, _rhs);
            }

        }

        static  class shr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shr<Rt,T>, Rt, T> {
            protected shr(shr<Rt,T> original){
                super(original);
            }
            @Override public shr<Rt,T> cloneMe(){
                return (new shr<Rt,T>(this));
            }
            public shr(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "shr_", _dest, _lhs, _rhs);
            }

        }

        static  class ushr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<ushr<Rt,T>, Rt, T> {
            protected ushr(ushr<Rt,T> original){
                super(original);
            }
            @Override public ushr<Rt,T> cloneMe(){
                return (new ushr<Rt,T>(this));
            }
            public ushr(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "ushr_", _dest, _lhs, _rhs);
            }

        }


        static  class and<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<and<Rt,T>, Rt, T> {
            protected and(and<Rt,T> original){
                super(original);
            }
            @Override public and<Rt,T> cloneMe(){
                return (new and<Rt,T>(this));
            }
            public and(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "and_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static  class or<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<or<Rt,T>, Rt, T> {
            protected or(or<Rt,T> original){
                super(original);
            }
            @Override public or<Rt,T> cloneMe(){
                return (new or<Rt,T>(this));
            }
            public or(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "or_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static  class xor<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<xor<Rt,T>, Rt, T> {
            protected xor(xor<Rt,T> original){
                super(original);
            }
            @Override public xor<Rt,T> cloneMe(){
                return (new xor<Rt,T>(this));
            }
            public xor(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "xor_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static class mov_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDest<mov_const<Rt,T,C>,Rt,T> {
            protected mov_const(mov_const<Rt,T,C> original){
                super(original);
                value = original.value;
            }
            @Override public mov_const<Rt,T,C> cloneMe(){
                return (new mov_const<Rt,T,C>(this));
            }
            C value;

            public mov_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, C _value) {
                super(_hsailStackFrame,_from, _dest);
                value = _value;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("mov_").movTypeName(getDest()).space().operandName(getDest()).separator().append(value).semicolon();

            }
        }

    static public  List<HSAILInstruction>  array_len(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add (new array_len(_hsailStackFrame,_i, new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> nyi(List<HSAILInstruction> _instructions, HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new nyi(_hsailStackFrame, _i));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
       _instructions.add(new field_store<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,1), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i, _hsailStackFrame.bottom, 1), new StackReg_ref(_i, _hsailStackFrame.bottom,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 1), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 1), new StackReg_ref(_i, _hsailStackFrame.bottom,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_s16(_i, _hsailStackFrame.bottom,1), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_u16(_i,_hsailStackFrame.bottom, 1), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_s8(_i,_hsailStackFrame.bottom, 1), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 1), new StackReg_ref(_i, _hsailStackFrame.bottom,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
       _instructions.add(new field_load<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i, _hsailStackFrame.bottom,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_s16(_i, _hsailStackFrame.bottom,0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_u16(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i, _hsailStackFrame.bottom,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_s8(_i, _hsailStackFrame.bottom,0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i, _hsailStackFrame.bottom,0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_s16(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_u16(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i, _hsailStackFrame.bottom,0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_s8(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i, _hsailStackFrame.bottom,0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ret_void(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new retvoid(_hsailStackFrame,_i));
        return(_instructions);
    }
    static public List<HSAILInstruction> ret_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> branch(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new branch(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), _i.getByteCode().getName(), _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> brn(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new brn(_hsailStackFrame, _i, _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> cbr(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new cbr(_hsailStackFrame, _i, _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_ref_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new cmp_ref(_hsailStackFrame,_i, "ne", new StackReg_ref(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_ref_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_ref(_hsailStackFrame,_i, "eq", new StackReg_ref(_i, _hsailStackFrame.bottom,0), new StackReg_ref(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32(_hsailStackFrame,_i, "ne", new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32(_hsailStackFrame,_i, "eq", new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32(_hsailStackFrame,_i, "lt", new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32(_hsailStackFrame,_i, "gt", new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32(_hsailStackFrame,_i, "ge", new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32(_hsailStackFrame,_i, "le", new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_le_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "le", new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_gt_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "gt", new StackReg_s32(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ge_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "ge", new StackReg_s32(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_lt_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "lt", new StackReg_s32(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_eq_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "eq", new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ne_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "ne", new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s64_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       Instruction lastInstruction = _i.getPrevPC();
       _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "le", new StackReg_s64(lastInstruction, _hsailStackFrame.bottom,0), new StackReg_s64(lastInstruction, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "ge", new StackReg_s64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_s64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "gt", new StackReg_s64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_s64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "lt", new StackReg_s64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_s64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "eq", new StackReg_s64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_s64(lastInstruction, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "ne", new StackReg_s64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_s64(lastInstruction, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_f64_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "le", new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "ge", new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "lt", new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "gt", new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "eq", new StackReg_f64(lastInstruction, _hsailStackFrame.bottom,0), new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "ne", new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_f32_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "le", new StackReg_f32(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_f32(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "ge", new StackReg_f32(lastInstruction, _hsailStackFrame.bottom,0), new StackReg_f32(lastInstruction, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "lt", new StackReg_f32(lastInstruction, _hsailStackFrame.bottom,0), new StackReg_f32(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "gt", new StackReg_f32(lastInstruction, _hsailStackFrame.bottom,0), new StackReg_f32(lastInstruction, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "eq", new StackReg_f32(lastInstruction, _hsailStackFrame.bottom,0), new StackReg_f32(lastInstruction,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "ne", new StackReg_f32(lastInstruction,_hsailStackFrame.bottom, 0), new StackReg_f32(lastInstruction, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s8_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s8,StackReg_s32,s8, s32>(_hsailStackFrame, _i, new StackReg_s8(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s16_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s16,StackReg_s32,s16, s32>(_hsailStackFrame, _i, new StackReg_s16(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_u16_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_u16,StackReg_s32,u16, s32>(_hsailStackFrame, _i, new StackReg_u16(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f32,StackReg_s32,f32, s32>(_hsailStackFrame, _i, new StackReg_f32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s64,StackReg_s32,s64, s32>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f64,StackReg_s32,f64, s32>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_ref_s32_1(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 1), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_ref_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s32_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s32,StackReg_s64,s32, s64>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f32,StackReg_s64,f32, s64>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f64,StackReg_s64,f64, s64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cvt_s32_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s32,StackReg_f32,s32, f32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f64,StackReg_f32,f64, f32>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s64,StackReg_f32,s64, f32>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s32_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s32,StackReg_f64,s32, f64>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_f64(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f32,StackReg_f64,f32, f64>(_hsailStackFrame, _i, new StackReg_f32(_i, _hsailStackFrame.bottom,0), new StackReg_f64(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s64,StackReg_f64,s64, f64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_const_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add_const<VarReg_s32, s32, Integer>(_hsailStackFrame, _i, new VarReg_s32(_i, _hsailStackFrame.bottom), new VarReg_s32(_i, _hsailStackFrame.bottom), ((InstructionSet.I_IINC) _i).getDelta()));
        return(_instructions);
    }
    static public List<HSAILInstruction> xor_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new xor<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> xor_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new xor<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> or_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new or<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> or_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new or<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom,0), new StackReg_s32(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> and_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new and<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> and_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new and<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ushr_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ushr<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ushr_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ushr<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shr_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new shr<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shr_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new shr<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shl_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new shl<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shl_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new shl<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new neg<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new neg<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new neg<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new neg<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new rem<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new rem<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new rem<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i, _hsailStackFrame.bottom,0), new StackReg_f64(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new rem<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i, _hsailStackFrame.bottom,0), new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new div<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new div<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new div<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new div<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i, _hsailStackFrame.bottom,0), new StackReg_f32(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mul<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mul<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mul<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i, _hsailStackFrame.bottom,0), new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mul<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new sub<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new sub<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new sub<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new sub<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i, _hsailStackFrame.bottom,0), new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,0), new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_s64(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_s32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_f64(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_f32(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 1), new StackReg_s16(_i,_hsailStackFrame.bottom, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 1), new StackReg_u16(_i,_hsailStackFrame.bottom, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 1), new StackReg_s32(_i,_hsailStackFrame.bottom, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 1), new StackReg_f32(_i,_hsailStackFrame.bottom, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 1), new StackReg_f64(_i,_hsailStackFrame.bottom, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.bottom,1), new StackReg_ref(_i,_hsailStackFrame.bottom, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.bottom,1), new StackReg_s8(_i,_hsailStackFrame.bottom, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.bottom,1), new StackReg_s64(_i,_hsailStackFrame.bottom, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mad(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _size){
       _instructions.add(new mad(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 1), new StackReg_ref(_i, _hsailStackFrame.bottom,1), new StackReg_ref(_i,_hsailStackFrame.bottom, 0), (long) _size));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_ref,StackReg_ref,ref,ref>(_hsailStackFrame, _i, new VarReg_ref(_i, _hsailStackFrame.bottom), new StackReg_ref(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_s32,StackReg_s32,s32,s32>(_hsailStackFrame, _i, new VarReg_s32(_i, _hsailStackFrame.bottom), new StackReg_s32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_f32,StackReg_f32,f32,f32>(_hsailStackFrame, _i, new VarReg_f32(_i, _hsailStackFrame.bottom), new StackReg_f32(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_f64,StackReg_f64,f64,f64>(_hsailStackFrame, _i, new VarReg_f64(_i, _hsailStackFrame.bottom), new StackReg_f64(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_s64,StackReg_s64,s64,s64>(_hsailStackFrame, _i, new VarReg_s64(_i, _hsailStackFrame.bottom), new StackReg_s64(_i,_hsailStackFrame.bottom, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> array_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.bottom,0), new StackReg_ref(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_u16(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_s16(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i, _hsailStackFrame.bottom,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> array_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_s8(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.bottom, 0), new StackReg_ref(_i,_hsailStackFrame.bottom, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f64_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_f64,VarReg_f64, f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), new VarReg_f64(_i, _hsailStackFrame.bottom)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f32_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_f32,VarReg_f32, f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), new VarReg_f32(_i, _hsailStackFrame.bottom)));
        return(_instructions);
    }

    static public List<HSAILInstruction> mov_s64_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_s64,VarReg_s64, s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.bottom,0), new VarReg_s64(_i, _hsailStackFrame.bottom)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s32_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_s32,VarReg_s32, s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.bottom,0), new VarReg_s32(_i, _hsailStackFrame.bottom)));
        return(_instructions);
    }


    static public List<HSAILInstruction> mov_ref_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_ref,VarReg_ref, ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.bottom,0), new VarReg_ref(_i, _hsailStackFrame.bottom)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s64_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, long _value){
        _instructions.add(new  mov_const<StackReg_s64,s64, Long>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.bottom, 0), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s32_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _value){
        _instructions.add(new  mov_const<StackReg_s32,s32, Integer>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, 0), _value));
        return(_instructions);
    }

    static public List<HSAILInstruction> mov_f64_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, double _value){
        _instructions.add(new  mov_const<StackReg_f64,f64, Double>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.bottom, 0), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f32_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, float _value){
        _instructions.add(new  mov_const<StackReg_f32,f32, Float>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.bottom, 0), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_ref(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_ref(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_f32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_f32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_f64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_f64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_s64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_s64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> workitemabsid_u32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new workitemabsid<VarReg_s32, s32>(_hsailStackFrame, _i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public void addmov(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, PrimitiveType _type, int _from, int _to) {
        if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32) {
            if (_type.equals(PrimitiveType.ref)) {
                _instructions.add(new mov<StackReg_ref,StackReg_ref,ref,ref>(_hsailStackFrame,_i, new StackReg_ref( _i,_hsailStackFrame.bottom, _to), new StackReg_ref(_i, _hsailStackFrame.bottom,_from)));
            } else if (_type.equals(PrimitiveType.s32)) {
                _instructions.add(new mov<StackReg_s32, StackReg_s32, s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.bottom, _to), new StackReg_s32(_i,_hsailStackFrame.bottom, _from)));
            } else {
                throw new IllegalStateException(" unknown prefix 1 prefix for first of DUP2");
            }

        } else {
            throw new IllegalStateException(" unknown prefix 2 prefix for DUP2");
        }
    }
    static public HSAILRegister getRegOfLastWriteToIndex(List<HSAILInstruction>_instructions,int _index) {

        int idx = _instructions.size();
        while (--idx >= 0) {
            HSAILInstruction i = _instructions.get(idx);
            if (i.dests != null) {
                for (HSAILRegister d : i.dests) {
                    if (d.index == _index) {
                        return (d);
                    }
                }
            }
        }


        return (null);
    }
    static public HSAILRegister addmov(List<HSAILInstruction>_instructions, HSAILStackFrame _hsailStackFrame, Instruction _i, int _from, int _to) {
        HSAILRegister r = getRegOfLastWriteToIndex(_instructions, _i.getPreStackBase() + _i.getMethod().getCodeEntry().getMaxLocals() + _from);
        if (r == null){
            System.out.println("damn!");
        }
        addmov(_instructions, _hsailStackFrame, _i, r.type, _from, _to);
        return (r);
    }

    // for reference
    static public void add(List<HSAILInstruction>_instructions, HSAILInstruction _regInstruction) {
        // before we add lets see if this is a redundant mov
        if ( _regInstruction.sources != null && _regInstruction.sources.length > 0) {
            for (int regIndex = 0; regIndex < _regInstruction.sources.length; regIndex++) {
                HSAILRegister r = (HSAILRegister)_regInstruction.sources[regIndex];
                if (r.isStack()) {
                    // look up the list of reg instructions for the parentHSAILStackFrame mov which assigns to r
                    int i = _instructions.size();
                    while ((--i) >= 0) {
                        if (_instructions.get(i) instanceof mov) {
                            // we have found a move
                            mov candidateForRemoval = (mov) _instructions.get(i);
                            if (candidateForRemoval.from.getBlock() == _regInstruction.from.getBlock()
                                    && candidateForRemoval.getDest().isStack() && candidateForRemoval.getDest().equals(r)) {
                                // so i may be a candidate if between i and instruction.size() i.dest() is not mutated
                                boolean mutated = false;
                                for (int x = i + 1; !mutated && x < _instructions.size(); x++) {
                                    if (_instructions.get(x).dests.length > 0 && _instructions.get(x).dests[0].equals(candidateForRemoval.getSrc())) {
                                        mutated = true;
                                    }
                                }
                                if (!mutated) {
                                    _instructions.remove(i);
                                    // removed mov
                                    _regInstruction.sources[regIndex] = candidateForRemoval.getSrc();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        _instructions.add(_regInstruction);
    }

   enum ParseState {NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64}
   ;
   public static void addInstructions(List<HSAILInstruction> instructions, List<HSAILStackFrame> _frameSet, Stack<HSAILStackFrame> _frames, ClassModel.ClassModelMethod  method){
      HSAILStackFrame hsailStackFrame = _frames.peek();
      ParseState parseState = ParseState.NONE;
      boolean inlining = true;
      boolean needsReturnBranch = false;
      for (Instruction i : method.getInstructions()) {

         switch (i.getByteCode()) {

            case ACONST_NULL:
               nyi(instructions, hsailStackFrame, i);
               break;
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case BIPUSH:
            case SIPUSH:
               mov_s32_const(instructions, hsailStackFrame, i,  i.asIntegerConstant().getValue());
               break;
            case LCONST_0:
            case LCONST_1:
               mov_s64_const(instructions, hsailStackFrame, i, i.asLongConstant().getValue());
               break;
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
               mov_f32_const(instructions, hsailStackFrame, i, i.asFloatConstant().getValue());
               break;
            case DCONST_0:
            case DCONST_1:
               mov_f64_const(instructions, hsailStackFrame, i, i.asDoubleConstant().getValue());

               break;
            // case BIPUSH: moved up
            // case SIPUSH: moved up

            case LDC:
            case LDC_W:
            case LDC2_W: {
               InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) i;

               ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
               if (e instanceof ClassModel.ConstantPool.DoubleEntry) {
                  mov_f64_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.DoubleEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.FloatEntry) {
                  mov_f32_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.FloatEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.IntegerEntry) {
                  mov_s32_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.IntegerEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.LongEntry) {
                  mov_s64_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.LongEntry) e).getValue());
               }

            }
            break;
            // case LLOAD: moved down
            // case FLOAD: moved down
            // case DLOAD: moved down
            //case ALOAD: moved down
            case ILOAD:
            case ILOAD_0:
            case ILOAD_1:
            case ILOAD_2:
            case ILOAD_3:
               mov_s32_var(instructions, hsailStackFrame, i);

               break;
            case LLOAD:
            case LLOAD_0:
            case LLOAD_1:
            case LLOAD_2:
            case LLOAD_3:
               mov_s64_var(instructions, hsailStackFrame, i);
               break;
            case FLOAD:
            case FLOAD_0:
            case FLOAD_1:
            case FLOAD_2:
            case FLOAD_3:

               mov_f32_var(instructions, hsailStackFrame, i);
               break;
            case DLOAD:
            case DLOAD_0:
            case DLOAD_1:
            case DLOAD_2:
            case DLOAD_3:

               mov_f64_var(instructions, hsailStackFrame, i);
               break;
            case ALOAD:
            case ALOAD_0:
            case ALOAD_1:
            case ALOAD_2:
            case ALOAD_3:
               mov_ref_var(instructions, hsailStackFrame, i);

               break;
            case IALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
               array_load_s32(instructions, hsailStackFrame, i);
               break;
            case LALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s64.getHsaBytes());
               array_load_s64(instructions, hsailStackFrame, i);
               break;
            case FALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.f32.getHsaBytes());
               array_load_f32(instructions, hsailStackFrame, i);
               break;
            case DALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.f64.getHsaBytes());
               array_load_f64(instructions, hsailStackFrame, i);
               break;
            case AALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.ref.getHsaBytes());
               array_load_ref(instructions, hsailStackFrame, i);
               break;
            case BALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s8.getHsaBytes());
               array_load_s8(instructions, hsailStackFrame, i);
               break;
            case CALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.u16.getHsaBytes());
               array_load_u16(instructions, hsailStackFrame, i);
               break;
            case SALOAD:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
               array_load_s16(instructions, hsailStackFrame, i);
               break;
            //case ISTORE: moved down
            // case LSTORE:  moved down
            //case FSTORE: moved down
            //case DSTORE:  moved down
            // case ASTORE: moved down
            case ISTORE:
            case ISTORE_0:
            case ISTORE_1:
            case ISTORE_2:
            case ISTORE_3:
               mov_var_s32(instructions, hsailStackFrame, i);

               break;
            case LSTORE:
            case LSTORE_0:
            case LSTORE_1:
            case LSTORE_2:
            case LSTORE_3:
               mov_var_s64(instructions, hsailStackFrame, i);

               break;
            case FSTORE:
            case FSTORE_0:
            case FSTORE_1:
            case FSTORE_2:
            case FSTORE_3:
               mov_var_f32(instructions, hsailStackFrame, i);
               break;
            case DSTORE:
            case DSTORE_0:
            case DSTORE_1:
            case DSTORE_2:
            case DSTORE_3:
               mov_var_f64(instructions, hsailStackFrame, i);
               break;
            case ASTORE:
            case ASTORE_0:
            case ASTORE_1:
            case ASTORE_2:
            case ASTORE_3:
               mov_var_ref(instructions, hsailStackFrame, i);
               break;
            case IASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
               array_store_s32(instructions, hsailStackFrame, i);
               break;
            case LASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s64.getHsaBytes());
               array_store_s64(instructions, hsailStackFrame, i);
               break;
            case FASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.f32.getHsaBytes());
               array_store_f32(instructions, hsailStackFrame, i);
               break;
            case DASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.f64.getHsaBytes());
               array_store_f64(instructions, hsailStackFrame, i);
               break;
            case AASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.ref.getHsaBytes());
               array_store_ref(instructions, hsailStackFrame, i);
               break;
            case BASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s8.getHsaBytes());
               array_store_s8(instructions, hsailStackFrame, i);
               break;
            case CASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.u16.getHsaBytes());
               array_store_u16(instructions, hsailStackFrame, i);
               break;
            case SASTORE:
               cvt_ref_s32_1(instructions, hsailStackFrame, i);
               mad(instructions, hsailStackFrame, i, PrimitiveType.s16.getHsaBytes());
               array_store_s16(instructions, hsailStackFrame, i);
               break;
            case POP:
               nyi(instructions, hsailStackFrame,i);
               break;
            case POP2:
               nyi(instructions, hsailStackFrame,i);
               break;
            case DUP:
               // add(new nyi(i));
               addmov(instructions, hsailStackFrame, i, 0, 1);
               break;
            case DUP_X1:
               nyi(instructions, hsailStackFrame,i);
               break;
            case DUP_X2:

               addmov(instructions, hsailStackFrame, i, 2, 3);
               addmov(instructions, hsailStackFrame, i, 1, 2);
               addmov(instructions, hsailStackFrame, i, 0, 1);
               addmov(instructions, hsailStackFrame, i, 3, 0);

               break;
            case DUP2:
               // DUP2 is problematic. DUP2 either dups top two items or one depending on the 'prefix' of the stack items.
               // To complicate this further HSA large model wants object/mem references to be 64 bits (prefix 2 in Java) whereas
               // in java object/array refs are 32 bits (prefix 1).
               addmov(instructions, hsailStackFrame, i, 0, 2);
               addmov(instructions, hsailStackFrame, i, 1, 3);
               break;
            case DUP2_X1:
               nyi(instructions, hsailStackFrame,i);
               break;
            case DUP2_X2:
               nyi(instructions, hsailStackFrame,i);
               break;
            case SWAP:
               nyi(instructions, hsailStackFrame,i);
               break;
            case IADD:
               add_s32(instructions, hsailStackFrame, i);
               break;
            case LADD:
               add_s64(instructions, hsailStackFrame, i);
               break;
            case FADD:
               add_f32(instructions, hsailStackFrame, i);
               break;
            case DADD:
               add_f64(instructions, hsailStackFrame, i);
               break;
            case ISUB:
               sub_s32(instructions, hsailStackFrame, i);
               break;
            case LSUB:
               sub_s64(instructions, hsailStackFrame, i);
               break;
            case FSUB:
               sub_f32(instructions, hsailStackFrame, i);
               break;
            case DSUB:
               sub_f64(instructions, hsailStackFrame, i);
               break;
            case IMUL:
               mul_s32(instructions, hsailStackFrame, i);
               break;
            case LMUL:
               mul_s64(instructions, hsailStackFrame, i);
               break;
            case FMUL:
               mul_f32(instructions, hsailStackFrame, i);
               break;
            case DMUL:
               mul_f64(instructions, hsailStackFrame, i);
               break;
            case IDIV:
               div_s32(instructions, hsailStackFrame, i);
               break;
            case LDIV:
               div_s64(instructions, hsailStackFrame, i);
               break;
            case FDIV:
               div_f32(instructions, hsailStackFrame, i);
               break;
            case DDIV:
               div_f64(instructions, hsailStackFrame, i);
               break;
            case IREM:
               rem_s32(instructions, hsailStackFrame, i);
               break;
            case LREM:
               rem_s64(instructions, hsailStackFrame, i);
               break;
            case FREM:
               rem_f32(instructions, hsailStackFrame, i);
               break;
            case DREM:
               rem_f64(instructions, hsailStackFrame, i);
               break;
            case INEG:
               neg_s32(instructions, hsailStackFrame, i);
               break;
            case LNEG:
               neg_s64(instructions, hsailStackFrame, i);
               break;
            case FNEG:
               neg_f32(instructions, hsailStackFrame, i);
               break;
            case DNEG:
               neg_f64(instructions, hsailStackFrame, i);
               break;
            case ISHL:
               shl_s32(instructions, hsailStackFrame, i);
               break;
            case LSHL:
               shl_s64(instructions, hsailStackFrame, i);
               break;
            case ISHR:
               shr_s32(instructions, hsailStackFrame, i);
               break;
            case LSHR:
               shr_s64(instructions, hsailStackFrame, i);
               break;
            case IUSHR:
               ushr_s32(instructions, hsailStackFrame, i);
               break;
            case LUSHR:
               ushr_s64(instructions, hsailStackFrame, i);
               break;
            case IAND:
               and_s32(instructions, hsailStackFrame, i);
               break;
            case LAND:
               and_s64(instructions, hsailStackFrame, i);
               break;
            case IOR:
               or_s32(instructions, hsailStackFrame, i);
               break;
            case LOR:
               or_s64(instructions, hsailStackFrame, i);
               break;
            case IXOR:
               xor_s32(instructions, hsailStackFrame, i);
               break;
            case LXOR:
               xor_s64(instructions, hsailStackFrame, i);
               break;
            case IINC:
               add_const_s32(instructions, hsailStackFrame, i);
               break;
            case I2L:
               cvt_s64_s32(instructions, hsailStackFrame, i);
               break;
            case I2F:
               cvt_f32_s32(instructions, hsailStackFrame, i);
               break;
            case I2D:
               cvt_f64_s32(instructions, hsailStackFrame, i);
               break;
            case L2I:
               cvt_s32_s64(instructions, hsailStackFrame, i);
               break;
            case L2F:
               cvt_f32_s64(instructions, hsailStackFrame, i);
               break;
            case L2D:
               cvt_f64_s64(instructions, hsailStackFrame, i);
               break;
            case F2I:
               cvt_s32_f32(instructions, hsailStackFrame, i);
               break;
            case F2L:
               cvt_s64_f32(instructions, hsailStackFrame, i);
               break;
            case F2D:
               cvt_f64_f32(instructions, hsailStackFrame, i);
               break;
            case D2I:
               cvt_s32_f64(instructions, hsailStackFrame, i);
               break;
            case D2L:
               cvt_s64_f64(instructions, hsailStackFrame, i);
               break;
            case D2F:
               cvt_f32_f64(instructions, hsailStackFrame, i);
               break;
            case I2B:
               cvt_s8_s32(instructions, hsailStackFrame, i);
               break;
            case I2C:
               cvt_u16_s32(instructions, hsailStackFrame, i);
               break;
            case I2S:
               cvt_s16_s32(instructions, hsailStackFrame, i);
               break;
            case LCMP:
               parseState = ParseState.COMPARE_S64;
               break;
            case FCMPL:
               parseState = ParseState.COMPARE_F32;
               break;
            case FCMPG:
               parseState = ParseState.COMPARE_F32;
               break;
            case DCMPL:
               parseState = ParseState.COMPARE_F64;
               break;
            case DCMPG:
               parseState = ParseState.COMPARE_F64;
               break;
            case IFEQ:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_eq(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_eq(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_eq(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_eq_const_0(instructions, hsailStackFrame, i);
               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFNE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_ne(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_ne(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_ne(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_ne_const_0(instructions, hsailStackFrame, i);
               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFLT:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_lt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_lt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_lt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_lt_const_0(instructions, hsailStackFrame, i);

               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFGE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_ge(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_ge(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_ge(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_ge_const_0(instructions, hsailStackFrame, i);

               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFGT:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_gt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_gt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_gt(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_gt_const_0(instructions, hsailStackFrame, i);

               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IFLE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                  cmp_f32_le(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  cmp_f64_le(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                  cmp_s64_le(instructions, hsailStackFrame, i);
                  parseState = ParseState.NONE;
               } else {
                  cmp_s32_le_const_0(instructions, hsailStackFrame, i);


               }
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPEQ:

               cmp_s32_eq(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);

               break;
            case IF_ICMPNE:
               cmp_s32_ne(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPLT:
               cmp_s32_lt(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPGE:
               cmp_s32_ge(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPGT:
               cmp_s32_gt(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ICMPLE:
               cmp_s32_le(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ACMPEQ:
               cmp_ref_eq(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case IF_ACMPNE:
               cmp_ref_ne(instructions, hsailStackFrame, i);
               cbr(instructions, hsailStackFrame, i);
               break;
            case GOTO:
               brn(instructions, hsailStackFrame, i);
               break;
            case IFNULL:
               branch(instructions, hsailStackFrame, i);
            case IFNONNULL:
               branch(instructions, hsailStackFrame, i);
            case GOTO_W:
               branch(instructions, hsailStackFrame, i);
               break;
            case JSR:
               nyi(instructions, hsailStackFrame, i);
               break;
            case RET:
               nyi(instructions, hsailStackFrame, i);
               break;
            case TABLESWITCH:
               nyi(instructions, hsailStackFrame, i);
               break;
            case LOOKUPSWITCH:
               nyi(instructions, hsailStackFrame, i);
               break;
            case IRETURN:
               if (inlining && _frames.size()>1){
                  if (i.getNextPC()!=null){
                    instructions.add(new mov<StackReg_s32, StackReg_s32, s32, s32>(hsailStackFrame, i, new StackReg_s32(i,hsailStackFrame.bottom,-hsailStackFrame.size), new StackReg_s32(i,hsailStackFrame.bottom,0)));           // -1 is wrong
                    instructions.add(new inlineReturnBrn(hsailStackFrame, i,hsailStackFrame.getUniqueName()));
                    needsReturnBranch=true;
                 } else if (needsReturnBranch){
                     instructions.add(new returnMov<StackReg_s32, StackReg_s32, s32, s32>(hsailStackFrame, i, new StackReg_s32(i,hsailStackFrame.bottom,-hsailStackFrame.size), new StackReg_s32(i,hsailStackFrame.bottom,0), hsailStackFrame.getUniqueName()));           // -1 is wrong
                  }else{
                     instructions.add(new mov<StackReg_s32, StackReg_s32, s32, s32>(hsailStackFrame, i, new StackReg_s32(i,hsailStackFrame.bottom,-hsailStackFrame.size), new StackReg_s32(i,hsailStackFrame.bottom,0)));           // -1 is wrong


                  }

               }else{
                  ret_s32(instructions, hsailStackFrame, i);
               }
               break;
            case LRETURN:
               ret_s64(instructions, hsailStackFrame, i);
               break;
            case FRETURN:
               ret_f32(instructions, hsailStackFrame, i);
               break;
            case DRETURN:
               ret_f64(instructions, hsailStackFrame, i);
               break;
            case ARETURN:

               ret_ref(instructions, hsailStackFrame, i);
               break;
            case RETURN:
               ret_void(instructions, hsailStackFrame, i);
               break;
            case GETSTATIC: {
               TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try {
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                  if (!type.isPrimitive()) {
                     static_field_load_ref(instructions, hsailStackFrame, i, f);
                  } else if (type.isInt()) {
                     static_field_load_s32(instructions, hsailStackFrame, i, f);
                  } else if (type.isFloat()) {
                     static_field_load_f32(instructions, hsailStackFrame, i, f);
                  } else if (type.isDouble()) {
                     static_field_load_f64(instructions, hsailStackFrame, i, f);
                  } else if (type.isLong()) {
                     static_field_load_s64(instructions, hsailStackFrame, i, f);
                  } else if (type.isChar()) {
                     static_field_load_u16(instructions, hsailStackFrame, i, f);
                  } else if (type.isShort()) {
                     static_field_load_s16(instructions, hsailStackFrame, i, f);
                  } else if (type.isChar()) {
                     static_field_load_s8(instructions, hsailStackFrame, i, f);
                  }
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               } catch (NoSuchFieldException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }


            }
            break;
            case GETFIELD: {
               // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try {
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if (!f.getType().isPrimitive()) {
                     field_load_ref(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(int.class)) {
                     field_load_s32(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(short.class)) {
                     field_load_s16(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(char.class)) {
                     field_load_u16(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(boolean.class)) {
                     field_load_s8(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(float.class)) {
                     field_load_f32(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(double.class)) {
                     field_load_f64(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(long.class)) {
                     field_load_s64(instructions, hsailStackFrame, i, f);
                  } else {
                     throw new IllegalStateException("unexpected get field type");
                  }
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               } catch (NoSuchFieldException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }


            }
            break;
            case PUTSTATIC:
               nyi(instructions, hsailStackFrame, i);
               break;
            case PUTFIELD: {
               // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try {
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if (!f.getType().isPrimitive()) {
                     field_store_ref(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(int.class)) {
                     field_store_s32(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(short.class)) {
                     field_store_s16(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(char.class)) {
                     field_store_u16(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(boolean.class)) {
                     field_store_s8(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(float.class)) {
                     field_store_f32(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(double.class)) {
                     field_store_f64(instructions, hsailStackFrame, i, f);
                  } else if (f.getType().equals(long.class)) {
                     field_store_s64(instructions, hsailStackFrame, i, f);
                  }   else {
                     throw new IllegalStateException("unexpected put field type");
                  }
               } catch (ClassNotFoundException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               } catch (NoSuchFieldException e) {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }


            }
            break;
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case INVOKEINTERFACE:
            case INVOKEDYNAMIC:
            {
               CallInfo callInfo = new CallInfo(i);
               InlineIntrinsicCall call = HSAILIntrinsics.getInlineIntrinsic(callInfo);
               if (call != null){
                  call.add(instructions, hsailStackFrame, i);
               }else{
                  if (inlining){
                     try{
                        Class theClass = Class.forName(callInfo.dotClassName);
                        ClassModel classModel = ClassModel.getClassModel(theClass);
                        ClassModel.ClassModelMethod calledMethod = classModel.getMethod(callInfo.name, callInfo.sig);
                        int bottom = i.getPreStackBase()+i.getMethod().getCodeEntry().getMaxLocals()+hsailStackFrame.bottom;                                    // var slot $0 is at register[varsBase]
                        int size = i.getMethod().getCodeEntry().getMaxLocals()-1;
                       // if (calledMethod.isVirtual()){
                          //  size++;
                      //  }
                         System.out.println("!!!!!size="+size+" bottom="+bottom);
                  
                        _frames.push(new HSAILStackFrame(hsailStackFrame,  calledMethod.getClassModel().getDotClassName()+"."+calledMethod.getName()+calledMethod.getDescriptor(), i.getThisPC(), bottom, size ));
                        _frameSet.add(_frames.peek());
                        addInstructions(instructions, _frames, _frames, calledMethod);
                        _frames.pop();



                        /*
                         if (!s.contains(i.from)) {
                   s.add(i.from);
                   if (i.from.isBranchTarget()) {
                      r.label(i.location).colon().nl();
                   }
                   if (r.isShowingComments()) {
                      r.nl().pad(1).lineCommentStart().append(i.location).mark().relpad(2).space().i(i.from).nl();
                   }
               }
               if (i instanceof retvoid){
                   r.pad(9).lineCommentStart().append(" ret removed as part of inlining");
               }else if (i instanceof ret){

                   r.pad(9).lineComment("ret removed and replaced by branch to end of code").nl();
                   r.pad(9).append("mov_").movTypeName(((ret) i).getSrc()).space().regPrefix(((ret) i).getSrc().type).append(base).separator().operandName(((ret) i).getSrc()).semicolon();
                   if (i != instructions.get(instructions.size()-1)){
                      endLabel = ((ret)i).endLabel;
                      r.nl().pad(9).append("brn ").label(endLabel).semicolon();

                   }
               }else{
                   r.pad(9);
                   i.render(r);
               }
                         */


                     }catch (ClassParseException cpe){

                     }catch (ClassNotFoundException cnf){

                     }

                  }  else {
                    // call(instructions, this, hsailStackFrame, i, callInfo);
                  }


               }
            }
            break;
            case NEW:
               nyi(instructions, hsailStackFrame, i);
               break;
            case NEWARRAY:
               nyi(instructions, hsailStackFrame, i);
               break;
            case ANEWARRAY:
               nyi(instructions, hsailStackFrame, i);
               break;
            case ARRAYLENGTH:
               array_len(instructions, hsailStackFrame, i);
               break;
            case ATHROW:
               nyi(instructions, hsailStackFrame, i);
               break;
            case CHECKCAST:
               nyi(instructions, hsailStackFrame, i);
               break;
            case INSTANCEOF:
               nyi(instructions, hsailStackFrame, i);
               break;
            case MONITORENTER:
               nyi(instructions, hsailStackFrame, i);
               break;
            case MONITOREXIT:
               nyi(instructions, hsailStackFrame, i);
               break;
            case WIDE:
               nyi(instructions, hsailStackFrame, i);
               break;
            case MULTIANEWARRAY:
               nyi(instructions, hsailStackFrame, i);
               break;
            case JSR_W:
               nyi(instructions, hsailStackFrame, i);
               break;

         }
         // lastInstruction = i;


      }

   }
}
