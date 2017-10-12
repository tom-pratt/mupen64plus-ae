/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *   Mupen64plus - asm_defines.c                                           *
 *   Mupen64Plus homepage: http://code.google.com/p/mupen64plus/           *
 *   Copyright (C) 2016 Bobby Smiles                                       *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.          *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/**
 * This file is to be compiled with the same compilation flags as the
 * mupen64plus-core, but without LTO / Global Optimizations
 * (those tends to inhibit effective creation of required symbols).
 * It's purpose is to help generate asm_defines headers
 * suitable for inclusion in assembly files.
 * This allow to effectively share struct definitions between C and assembly
 * files.
 */

#include "device/device.h"
#include "device/memory/memory.h"
#include "device/r4300/new_dynarec/new_dynarec.h"
#include "device/r4300/r4300_core.h"
#include "device/ri/ri_controller.h"

#include <stddef.h>

#define HEX(n) ((n) >= 10 ? ('a' + ((n) - 10)) : ('0' + (n)))

/* Creates a structure whose bytes form a string like
 * "\n@ASM_DEFINE offsetof_blah_blah 0xdeadbeef\n"
 *
 * This should appear somewhere in the object file, and is distinctive enough
 * that it shouldn't appear by chance.  Thus we can pipe the object file
 * directly to awk, and extract the values without having to use
 * platform-specific tools (e.g. objdump, dumpbin, nm).
 */
#define _DEFINE(str, sym, val) \
    const struct { \
        char before[sizeof(str)-1]; \
        char hexval[8]; \
        char after; \
        char ensure_32bit[(val) > 0xffffffff ? -1 : 1]; \
    } sym = { \
        str, \
        { \
            HEX(((val) >> 28) & 0xf), \
            HEX(((val) >> 24) & 0xf), \
            HEX(((val) >> 20) & 0xf), \
            HEX(((val) >> 16) & 0xf), \
            HEX(((val) >> 12) & 0xf), \
            HEX(((val) >>  8) & 0xf), \
            HEX(((val) >>  4) & 0xf), \
            HEX(((val) >>  0) & 0xf) \
        }, \
        '\n', \
        {0} \
    };

/* Export member m of structure s.
 * Suitable parsing of corresponding object file (with strings) can be used to
 * generate header suitable for inclusion in assembly files.
 */
#define DEFINE(s, m) \
    _DEFINE("\n@ASM_DEFINE offsetof_struct_" #s "_" #m " 0x", \
            __offsetof_struct_##s##_##m, \
            offsetof(struct s, m));


/* Structure members definitions */
DEFINE(device, r4300);

#if NEW_DYNAREC != NEW_DYNAREC_ARM
/* ARM dynarec uses a different memory layout */
DEFINE(r4300_core, regs);
DEFINE(r4300_core, hi);
DEFINE(r4300_core, lo);

DEFINE(r4300_core, stop);
#endif

#if defined(__x86_64__)
DEFINE(r4300_core, save_rsp);
DEFINE(r4300_core, save_rip);
#else
DEFINE(r4300_core, save_ebp);
DEFINE(r4300_core, save_esp);
DEFINE(r4300_core, save_ebx);
DEFINE(r4300_core, save_esi);
DEFINE(r4300_core, save_edi);
DEFINE(r4300_core, save_eip);
#endif
DEFINE(r4300_core, return_address);

DEFINE(r4300_core, cp0);
#if NEW_DYNAREC != NEW_DYNAREC_ARM
/* ARM dynarec uses a different memory layout */
DEFINE(cp0, regs);
DEFINE(cp0, next_interrupt);
#endif
DEFINE(cp0, last_addr);
DEFINE(cp0, count_per_op);
DEFINE(cp0, tlb);

DEFINE(tlb, entries);
DEFINE(tlb, LUT_r);
DEFINE(tlb, LUT_w);

DEFINE(r4300_core, cached_interp);
DEFINE(cached_interp, invalid_code);

#ifdef NEW_DYNAREC
DEFINE(r4300_core, new_dynarec_hot_state);
#if NEW_DYNAREC == NEW_DYNAREC_X86
DEFINE(new_dynarec_hot_state, cycle_count);
DEFINE(new_dynarec_hot_state, last_count);
DEFINE(new_dynarec_hot_state, pending_exception);
DEFINE(new_dynarec_hot_state, pcaddr);
DEFINE(new_dynarec_hot_state, branch_target);
DEFINE(new_dynarec_hot_state, fake_pc);
DEFINE(new_dynarec_hot_state, mini_ht);
DEFINE(new_dynarec_hot_state, restore_candidate);
DEFINE(new_dynarec_hot_state, memory_map);
#elif NEW_DYNAREC == NEW_DYNAREC_ARM
DEFINE(r4300_core, extra_memory);
DEFINE(new_dynarec_hot_state, dynarec_local);
DEFINE(new_dynarec_hot_state, next_interrupt);
DEFINE(new_dynarec_hot_state, cycle_count);
DEFINE(new_dynarec_hot_state, last_count);
DEFINE(new_dynarec_hot_state, pending_exception);
DEFINE(new_dynarec_hot_state, pcaddr);
DEFINE(new_dynarec_hot_state, stop);
DEFINE(new_dynarec_hot_state, invc_ptr);
DEFINE(new_dynarec_hot_state, fcr0);
DEFINE(new_dynarec_hot_state, fcr31);
DEFINE(new_dynarec_hot_state, regs);
DEFINE(new_dynarec_hot_state, hi);
DEFINE(new_dynarec_hot_state, lo);
DEFINE(new_dynarec_hot_state, cp0_regs);
DEFINE(new_dynarec_hot_state, cp1_regs_simple);
DEFINE(new_dynarec_hot_state, cp1_regs_double);
DEFINE(new_dynarec_hot_state, rounding_modes);
DEFINE(new_dynarec_hot_state, branch_target);
DEFINE(new_dynarec_hot_state, pc);
DEFINE(new_dynarec_hot_state, fake_pc);
DEFINE(new_dynarec_hot_state, mini_ht);
DEFINE(new_dynarec_hot_state, restore_candidate);
DEFINE(new_dynarec_hot_state, memory_map);
#endif
#endif
