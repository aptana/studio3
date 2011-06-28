==PREFS==
js.formatter.spaces.after.commas=1
js.formatter.spaces.after.assignment.operator=1
js.formatter.spaces.after.arithmetic.operator=1
js.formatter.spaces.after.conditional.operator=1
js.formatter.indent.case.body=true
js.formatter.indent.switch.body=true
js.formatter.spaces.before.for.semicolon.operator=0
js.formatter.spaces.before.parentheses=0
js.formatter.spaces.after.semicolon.operator=1
js.formatter.spaces.before.postfix.operator=0
js.formatter.spaces.after.postfix.operator=0
js.formatter.spaces.before.relational.operator=1
js.formatter.spaces.before.case.colon.operator=1
js.formatter.spaces.before.unary.operator=0
js.formatter.wrap.comments=false
js.formatter.indent.group.body=true
js.formatter.line.after.function.declaration=1
js.formatter.spaces.after.concatenation.operator=1
js.formatter.wrap.comments.length=80
js.formatter.spaces.before.conditional.operator=1
js.formatter.formatter.tabulation.size=4
js.formatter.spaces.after.relational.operator=1
js.formatter.spaces.after.case.colon.operator=1
js.formatter.spaces.before.prefix.operator=0
js.formatter.indent.blocks=true
js.formatter.spaces.before.concatenation.operator=1
js.formatter.line.preserve=1
js.formatter.brace.position.case.block=same.line
js.formatter.brace.position.switch.block=same.line
js.formatter.formatter.tabulation.char=editor
js.formatter.indent.function.body=true
js.formatter.line.after.function.declaration.expression=0
js.formatter.formatter.indentation.size=4
js.formatter.newline.before.name.value.pairs=true
js.formatter.spaces.after.parentheses=0
js.formatter.spaces.after.for.semicolon.operator=1
js.formatter.newline.before.if.in.elseif=false
js.formatter.spaces.before.key.value.operator=1
js.formatter.spaces.before.commas=0
js.formatter.spaces.after.unary.operator=0
js.formatter.spaces.before.arithmetic.operator=1
js.formatter.spaces.before.assignment.operator=1
js.formatter.newline.before.dowhile=false
js.formatter.newline.before.else=false
js.formatter.spaces.before.semicolon.operator=0
js.formatter.newline.before.finally=false
js.formatter.newline.before.catch=false
js.formatter.spaces.after.prefix.operator=0
js.formatter.brace.position.function.declaration=same.line
js.formatter.spaces.after.key.value.operator=1
js.formatter.brace.position.blocks=same.line
==CONTENT==
function Mb(Ga, Nb) {
	var base, ha, Ob, Pb;
	switch ((Ga & 7) | ((Ga >> 3) & 0x18)) {
		case 0x04:
			Ob = ((Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua];
			Hb++; ;
			base = Ob & 7;
			if (base == 5)
			{
				ha = Lb();
			} else
			{
				ha = za[base];
				if (Nb && base == 4)
					ha = (ha + Nb) & -1;
			}
			Pb = (Ob >> 3) & 7;
			if (Pb != 4)
			{
				ha = (ha + (za[Pb] << (Ob >> 6))) & -1;
			}
			break;
		case 0x0c:
			Ob = ((Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua];
			Hb++; ;
			ha = ((((Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua]) << 24) >> 24;
			Hb++; ;
			base = Ob & 7;
			ha = (ha + za[base]) & -1;
			if (Nb && base == 4)
				ha = (ha + Nb) & -1;
			Pb = (Ob >> 3) & 7;
			if (Pb != 4)
			{
				ha = (ha + (za[Pb] << (Ob >> 6))) & -1;
			}
			break;
		case 0x14:
			Ob = ((Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua];
			Hb++; ;
			ha = Lb();
			base = Ob & 7;
			ha = (ha + za[base]) & -1;
			if (Nb && base == 4)
				ha = (ha + Nb) & -1;
			Pb = (Ob >> 3) & 7;
			if (Pb != 4)
			{
				ha = (ha + (za[Pb] << (Ob >> 6))) & -1;
			}
			break;
		case 0x05:
			ha = Lb();
			break;
		case 0x00:
		case 0x01:
		case 0x02:
		case 0x03:
		case 0x06:
		case 0x07:
			base = Ga & 7;
			ha = za[base];
			break;
		case 0x08:
		case 0x09:
		case 0x0a:
		case 0x0b:
		case 0x0d:
		case 0x0e:
		case 0x0f:
			ha = ((((Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua]) << 24) >> 24;
			Hb++; ;
			base = Ga & 7;
			ha = (ha + za[base]) & -1;
			break;
		case 0x10:
		case 0x11:
		case 0x12:
		case 0x13:
		case 0x15:
		case 0x16:
		case 0x17:
			ha = Lb();
			base = Ga & 7;
			ha = (ha + za[base]) & -1;
			break;
		default:
			throw "get_modrm";
	}
	if (Fa & 0x000f)
	{
		ha = (ha + ya.segs[(Fa & 0x000f) - 1].base) & -1;
	}
	return ha;
}
==FORMATTED==
function Mb(Ga, Nb) {
    var base, ha, Ob, Pb;
    switch ((Ga & 7) | ((Ga >> 3) & 0x18)) {
        case 0x04:
            Ob = (( Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua];
            Hb++;
            ;
            base = Ob & 7;
            if(base == 5) {
                ha = Lb();
            } else {
                ha = za[base];
                if(Nb && base == 4)
                    ha = (ha + Nb) & -1;
            }
            Pb = (Ob >> 3) & 7;
            if(Pb != 4) {
                ha = (ha + (za[Pb] << (Ob >> 6))) & -1;
            }
            break;
        case 0x0c:
            Ob = (( Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua];
            Hb++;
            ;
            ha = (((( Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua]) << 24) >> 24;
            Hb++;
            ;
            base = Ob & 7;
            ha = (ha + za[base]) & -1;
            if(Nb && base == 4)
                ha = (ha + Nb) & -1;
            Pb = (Ob >> 3) & 7;
            if(Pb != 4) {
                ha = (ha + (za[Pb] << (Ob >> 6))) & -1;
            }
            break;
        case 0x14:
            Ob = (( Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua];
            Hb++;
            ;
            ha = Lb();
            base = Ob & 7;
            ha = (ha + za[base]) & -1;
            if(Nb && base == 4)
                ha = (ha + Nb) & -1;
            Pb = (Ob >> 3) & 7;
            if(Pb != 4) {
                ha = (ha + (za[Pb] << (Ob >> 6))) & -1;
            }
            break;
        case 0x05:
            ha = Lb();
            break;
        case 0x00:
        case 0x01:
        case 0x02:
        case 0x03:
        case 0x06:
        case 0x07:
            base = Ga & 7;
            ha = za[base];
            break;
        case 0x08:
        case 0x09:
        case 0x0a:
        case 0x0b:
        case 0x0d:
        case 0x0e:
        case 0x0f:
            ha = (((( Ua = Za[Hb >>> 12]) == -1) ? Jb(Hb) : Ra[Hb ^ Ua]) << 24) >> 24;
            Hb++;
            ;
            base = Ga & 7;
            ha = (ha + za[base]) & -1;
            break;
        case 0x10:
        case 0x11:
        case 0x12:
        case 0x13:
        case 0x15:
        case 0x16:
        case 0x17:
            ha = Lb();
            base = Ga & 7;
            ha = (ha + za[base]) & -1;
            break;
        default:
            throw "get_modrm";
    }
    if(Fa & 0x000f) {
        ha = (ha + ya.segs[(Fa & 0x000f) - 1].base) & -1;
    }
    return ha;
}