package com.dq.itopic.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * 计算当前字数的 EditText
 * 
 * @author Administrator
 * 
 */
public class InputControlEditText extends AppCompatEditText {

	private int maxInputLength;
	public InputLengthHintListener mInputLengthHintListener;
	public FilterChineseListener mfChineseListener;
	public GetInputLengthListener mGetInputLengthListener;
	private MyWatcher mWatcher;

	public InputControlEditText(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public InputControlEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InputControlEditText(Context context) {
		super(context);
	}

	public interface FilterChineseListener {
		void inputChineseHint();
	}

	public void setOnFilterChineseListener(FilterChineseListener listener) {
		mfChineseListener = listener;
		if (mWatcher == null) {
			mWatcher = new MyWatcher();
		}

		addTextChangedListener(mWatcher);
	}

	public interface InputLengthHintListener {
		void onOverFlowHint();
	}

	public void setOnMaxInputListener(int maxInputLength,
			InputLengthHintListener Listener) {
		this.maxInputLength = maxInputLength;
		mInputLengthHintListener = Listener;

		if (mWatcher == null) {
			mWatcher = new MyWatcher();
		}

		addTextChangedListener(mWatcher);

	}

	public interface GetInputLengthListener {
		void getInputLength(int length);
	}

	public void setOnGetInputLengthListener(GetInputLengthListener listener) {
		mGetInputLengthListener = listener;
		if (mWatcher == null) {
			mWatcher = new MyWatcher();
		}
		addTextChangedListener(mWatcher);
	}

	private class MyWatcher implements TextWatcher {
		private CharSequence temp;
		private int editEnd;
		private int editStart;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			editStart = getSelectionStart();
			editEnd = getSelectionEnd();

			if (s.toString().length() > maxInputLength) {
				if (mInputLengthHintListener != null) {
					mInputLengthHintListener.onOverFlowHint();
					if (editStart > 1) {
						s.delete(editStart - 1, editEnd);
					}else{
						s.delete(maxInputLength - 1, s.length());
					}
					int tempSelection = editStart;
					setText(s);
					setSelection(tempSelection);
				}
			}

			if (mGetInputLengthListener != null) {
				mGetInputLengthListener.getInputLength(temp.length());
			}

			if (mfChineseListener != null) {
				for (int i = 0; i < temp.length(); i++) {
					if (isChinese(temp.charAt(i))) {
						mfChineseListener.inputChineseHint();
						s.delete(editStart - 1, editEnd);
						int tempSelection = editStart;
						setText(s);
						setSelection(tempSelection);
					}
				}
			}
		}
	}
	
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

}
