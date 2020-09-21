package jledger.util;

import jledger.core.Content;

public class PrimitiveLayouts {

	/**
	 * Describes a fixed-width 8bit signed integer.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Content.Layout INT8(byte v) {
		return new AbstractLayouts.StaticTerminal() {
			@Override
			public Content.Blob initialise(Content.Blob blob, int offset) {
				return write_i8(v, blob, offset);
			}

			@Override
			public int size(Content.Blob blob, int offset) {
				return 1;
			}

			@Override
			public byte read_i8(Content.Blob blob, int offset) {
				return blob.read(offset);
			}

			@Override
			public Content.Blob write_i8(byte value, Content.Blob blob, int offset) {
				return blob.write(offset, value);
			}

			@Override
			public int size() {
				return 1;
			}
		};
	}

	/**
	 * Describes a fixed-width 16bit signed integer with a big-endian orientation.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Content.Layout INT16(short v) {
		return new AbstractLayouts.StaticTerminal() {

			@Override
			public Content.Blob initialise(Content.Blob blob, int offset) {
				return write_i16(v, blob, offset);
			}

			@Override
			public int size(Content.Blob blob, int offset) {
				return 2;
			}

			@Override
			public int size() {
				return 2;
			}

			@Override
			public short read_i16(Content.Blob blob, int offset) {
				// FIXME: faster API would be nice
				byte b1 = blob.read(offset);
				byte b2 = blob.read(offset + 1);
				// Recombine bytes
				return (short) ((b1 << 8) | b2);
			}

			@Override
			public Content.Blob write_i16(short value, Content.Blob blob, int offset) {
				// Convert value into bytes
				byte b1 = (byte) ((value >> 8) & 0xFF);
				byte b2 = (byte) (value & 0xFF);
				// FIXME: faster API would be nice
				return blob.replace(offset, 2, new byte[] { b1, b2 });
			}

			@Override
			public Content.Blob insert_i16(short value, Content.Blob blob, int offset) {
				// Convert value into bytes
				byte b1 = (byte) ((value >> 8) & 0xFF);
				byte b2 = (byte) (value & 0xFF);
				// FIXME: faster API would be nice
				return blob.replace(offset, 0, new byte[] { b1, b2 });
			}
		};
	}


	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Content.Layout INT32(int v) {
		return new AbstractLayouts.StaticTerminal() {

			@Override
			public Content.Blob initialise(Content.Blob blob, int offset) {
				return write_i32(v, blob, offset);
			}

			@Override
			public int size(Content.Blob blob, int offset) {
				return 4;
			}

			@Override
			public int size() {
				return 4;
			}

			@Override
			public int read_i32(Content.Blob blob, int offset) {
				// FIXME: faster API would be nice
				byte b1 = blob.read(offset);
				byte b2 = blob.read(offset + 1);
				byte b3 = blob.read(offset + 2);
				byte b4 = blob.read(offset + 3);
				// Recombine bytes
				return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
			}

			@Override
			public Content.Blob write_i32(int value, Content.Blob blob, int offset) {
				// Convert value into bytes
				byte b1 = (byte) ((value >> 24) & 0xFF);
				byte b2 = (byte) ((value >> 16) & 0xFF);
				byte b3 = (byte) ((value >> 8) & 0xFF);
				byte b4 = (byte) (value & 0xFF);
				// FIXME: faster API would be nice
				return blob.replace(offset, 4, new byte[] { b1, b2, b3, b4 });
			}

			@Override
			public Content.Blob insert_i32(int value, Content.Blob blob, int offset) {
				// Convert value into bytes
				byte b1 = (byte) ((value >> 24) & 0xFF);
				byte b2 = (byte) ((value >> 16) & 0xFF);
				byte b3 = (byte) ((value >> 8) & 0xFF);
				byte b4 = (byte) (value & 0xFF);
				// FIXME: faster API would be nice
				return blob.replace(offset, 0, new byte[] { b1, b2, b3, b4 });
			}

		};
	}

	/**
	 * Describes a fixed-width 8bit signed integer with a big-endian orientation
	 * and an initial value of zero.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Content.Layout INT8 = INT8((byte) 0);

	/**
	 * Describes a fixed-width 16bit signed integer with a big-endian orientation
	 * and an initial value of zero.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Content.Layout INT16 = INT16((short) 0);
	/**
	 * Describes a fixed-width 32bit signed integer with a big-endian orientation
	 * and an initial value of zero.
	 *
	 * @Param v Default initial value
	 * @return
	 */
	public static final Content.Layout INT32 = INT32(0);
}
